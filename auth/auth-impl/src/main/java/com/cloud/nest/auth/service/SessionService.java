package com.cloud.nest.auth.service;

import com.cloud.nest.auth.exception.AuthError;
import com.cloud.nest.auth.exception.AuthException;
import com.cloud.nest.auth.inout.SessionStatus;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.auth.jwt.JwtProperties;
import com.cloud.nest.auth.jwt.TokenSerializer;
import com.cloud.nest.auth.mapper.AuthMapper;
import com.cloud.nest.auth.model.*;
import com.cloud.nest.auth.repository.SessionHistoryRepository;
import com.cloud.nest.auth.repository.SessionRepository;
import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.platform.model.request.ClientRequestDetails;
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

import static java.util.concurrent.TimeUnit.MINUTES;

@Log4j2
@Service
@RequiredArgsConstructor
public class SessionService {

    private final AuthMapper authMapper;
    private final UserService userService;
    private final JwtProperties jwtProperties;
    private final TokenSerializer tokenSerializer;
    private final SessionRepository sessionRepository;
    private final SessionHistoryRepository sessionHistoryRepository;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(fixedRateString = "${security.jwt.session-cleanup-interval}", timeUnit = MINUTES)
    void cleanUpExpiredSessions() {
        transactionTemplate.executeWithoutResult(ts -> {
            final int deletedSessions = sessionRepository.deleteExpiredSessions();
            log.info("Deleted expired sessions: {}", deletedSessions);
        });
    }

    @Transactional(readOnly = true)
    @Nullable
    public SessionRecord findById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Transactional(readOnly = true)
    public boolean sessionExistsForIpAddress(String clientIpAddress) {
        return sessionRepository.existsActiveSession(clientIpAddress);
    }

    @Transactional
    @NotNull
    public TokenSession createSession(@NotNull SessionProperties sessionProperties) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expiresAt = now.plus(jwtProperties.getAccessTtl());

        final SessionRecord sessionRecord = authMapper.toSessionRecord(sessionProperties, now, expiresAt);
        sessionRepository.insert(sessionRecord);

        final SessionHistoryRecord sessionHistoryRecord = authMapper.toSessionHistoryRecord(sessionRecord);
        sessionHistoryRepository.save(sessionHistoryRecord);

        final ImmutablePair<AccessToken, RefreshToken> tokens = createTokens(sessionRecord, sessionProperties.requestDetails());
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
    public TokenSession refreshSession(@NotNull RefreshToken refreshToken, @NotNull ClientRequestDetails requestDetails) {
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
                SessionProperties.builder()
                        .userId(refreshToken.getSubjectV3())
                        .username(refreshToken.getSubjectV2())
                        .requestDetails(requestDetails)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<SessionHistoryOut> getSessionHistoryByUserId(Long userId, int offset, int limit) {
        return sessionHistoryRepository.findAllByUserIdOrderedByCreated(userId, offset, limit)
                .stream()
                .map(authMapper::toSessionHistoryOut)
                .toList();
    }

    public boolean isSessionActive(@Nullable SessionRecord sessionRecord) {
        return sessionRecord != null && sessionRecord.getStatus().equals(SessionStatus.ACTIVE.name());
    }

    @Transactional
    public void updateSessionRecord(@NotNull SessionRecord sessionRecord) {
        sessionRepository.update(sessionRecord);
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
