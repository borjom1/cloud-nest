package com.cloud.nest.auth.service.session;

import com.cloud.nest.auth.exception.AuthError;
import com.cloud.nest.auth.exception.AuthException;
import com.cloud.nest.auth.inout.SessionStatus;
import com.cloud.nest.auth.inout.response.ActiveSessionOut;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.auth.jwt.JwtProperties;
import com.cloud.nest.auth.jwt.TokenSerializer;
import com.cloud.nest.auth.mapper.AuthMapper;
import com.cloud.nest.auth.model.*;
import com.cloud.nest.auth.repository.SessionHistoryRepository;
import com.cloud.nest.auth.repository.SessionRepository;
import com.cloud.nest.auth.service.UserService;
import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.common.ObjectMapperUtils;
import com.cloud.nest.platform.model.auth.UserRole;
import com.cloud.nest.platform.model.exception.UnexpectedException;
import com.cloud.nest.platform.model.request.ClientRequestDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtSessionManager implements SessionManager, SessionProvider {

    private final AuthMapper authMapper;
    private final UserService userService;
    private final JwtProperties jwtProperties;
    private final TokenSerializer tokenSerializer;
    private final SessionRepository sessionRepository;
    private final SessionHistoryRepository sessionHistoryRepository;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRateString = "${security.jwt.session-cleanup-interval}", timeUnit = MINUTES)
    void cleanUpExpiredSessions() {
        transactionTemplate.executeWithoutResult(ts -> {
            final int deletedSessions = sessionRepository.deleteExpiredSessions();
            log.info("Deleted expired sessions: {}", deletedSessions);
        });
    }

    @Transactional(readOnly = true)
    @Nullable
    @Override
    public SessionRecord findById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Transactional
    @Override
    public TokenSession createSession(@NotNull SessionCreate sessionCreate) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expiresAt = now.plus(jwtProperties.getAccessTtl());
        final List<UserRole> roles = userService.getUserRoles(sessionCreate.userId());
        final String serializedRoles = ObjectMapperUtils.toJson(objectMapper, roles);

        final SessionRecord sessionRecord = authMapper.toSessionRecord(sessionCreate, now, expiresAt, serializedRoles);
        sessionRepository.insert(sessionRecord);

        final SessionHistoryRecord sessionHistoryRecord = authMapper.toSessionHistoryRecord(sessionRecord);
        sessionHistoryRepository.insert(sessionHistoryRecord);

        final ImmutablePair<AccessToken, RefreshToken> tokens = createTokens(sessionRecord, sessionCreate.requestDetails());
        final String serializedAccessToken = tokenSerializer.serializeToken(tokens.getLeft());
        final String serializedRefreshToken = tokenSerializer.serializeToken(tokens.getRight());

        return TokenSession.builder()
                .accessToken(serializedAccessToken)
                .refreshToken(serializedRefreshToken)
                .accessTokenTtl(jwtProperties.getAccessTtl())
                .refreshTokenTtl(jwtProperties.getRefreshTtl())
                .build();
    }

    @Transactional
    @NotNull
    @Override
    public TokenSession refreshSession(RefreshToken refreshToken, ClientRequestDetails requestDetails) {
        if (!refreshToken.getAuthorities().contains(TokenAuthority.REFRESH)) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS);
        }

        if (!refreshToken.getClientIp().equals(requestDetails.clientIp())) {
            throw new AuthException(AuthError.SESSION_MISMATCH);
        }

        final SessionRecord sessionRecord = sessionRepository.findById(refreshToken.getSubject());
        if (isSessionActive(sessionRecord)) {
            sessionRecord.setStatus(SessionStatus.DISABLED.name());
            sessionRepository.update(sessionRecord);
        } else if (userService.getById(refreshToken.getSubjectV3()).isEmpty()) {
            throw new AuthException(AuthError.REFRESH_NOT_AVAILABLE);
        }

        return createSession(
                SessionCreate.builder()
                        .userId(refreshToken.getSubjectV3())
                        .username(refreshToken.getSubjectV2())
                        .requestDetails(requestDetails)
                        .build()
        );
    }

    @Transactional
    public void logout(UserAuthSession session) {
        final SessionRecord record = sessionRepository.findById(session.sessionId());
        if (record == null) {
            throw new UnexpectedException("Unable to perform logout due to session absence");
        }
        record.setStatus(SessionStatus.DISABLED.name());
        sessionRepository.update(record);
    }

    @Transactional
    @Override
    public void updateLastActive(SessionRecord record) {
        final var now = LocalDateTime.now();
        record.setLastActive(now);
        sessionRepository.update(record);
        sessionHistoryRepository.updateLastActiveBySessionIdAndUserId(
                record.getId(),
                record.getUserId(),
                now
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ActiveSessionOut> getActiveSessionsByUserId(Long userId) {
        return sessionRepository.findAllActiveByUserId(userId)
                .stream()
                .map(authMapper::toActiveSessionOut)
                .sorted(comparing(ActiveSessionOut::lastActive).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<SessionHistoryOut> getSessionHistoryByUserId(Long userId, int offset, int limit) {
        return sessionHistoryRepository.findAllByUserIdOrderedByCreated(userId, offset, limit)
                .stream()
                .map(authMapper::toSessionHistoryOut)
                .toList();
    }

    @Override
    public boolean isSessionActive(@Nullable SessionRecord record) {
        return record != null && record.getStatus().equals(SessionStatus.ACTIVE.name());
    }

    @Transactional
    @Override
    public void deactivateAllSessionsByUserId(Long userId) {
        sessionRepository.deactivateAllSessionsByUserId(userId);
    }

    @Override
    public List<UserRole> extractRoles(SessionRecord record) {
        return record.getJsonProperties() == null
                ? Collections.emptyList()
                : ObjectMapperUtils.toParametrizedObject(objectMapper, record.getJsonProperties(), new TypeReference<>() {
        });
    }

    /**
     * @return Pair of access token as the left value, and refresh token as the right value
     */
    @NotNull
    private ImmutablePair<AccessToken, RefreshToken> createTokens(
            @NotNull SessionRecord record,
            @NotNull ClientRequestDetails requestDetails
    ) {
        final AccessToken accessToken = AccessToken.builder()
                .subject(record.getId())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(record.getCreated().atZone(ZoneId.systemDefault()).toInstant())
                .expireAt(record.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant())
                .authorities(Collections.emptySet())
                .build();

        final RefreshToken refreshToken = RefreshToken.of(accessToken);
        refreshToken.setSubjectV2(record.getUsername());
        refreshToken.setSubjectV3(record.getUserId());
        refreshToken.setClientIp(requestDetails.clientIp());
        refreshToken.setExpireAt(accessToken.getIssuedAt().plus(jwtProperties.getRefreshTtl()));
        refreshToken.getAuthorities().add(TokenAuthority.REFRESH);

        return ImmutablePair.of(accessToken, refreshToken);
    }
}
