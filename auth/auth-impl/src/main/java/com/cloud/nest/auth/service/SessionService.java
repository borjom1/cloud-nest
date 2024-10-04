package com.cloud.nest.auth.service;

import com.cloud.nest.auth.inout.SessionStatus;
import com.cloud.nest.auth.jwt.JwtProperties;
import com.cloud.nest.auth.jwt.TokenSerializer;
import com.cloud.nest.auth.model.SessionProperties;
import com.cloud.nest.auth.model.Token;
import com.cloud.nest.auth.model.TokenAuthority;
import com.cloud.nest.auth.model.TokenSession;
import com.cloud.nest.auth.repository.SessionRepository;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class SessionService {

    private final JwtProperties jwtProperties;
    private final TokenSerializer tokenSerializer;
    private final SessionRepository sessionRepository;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(fixedRate = 30L, timeUnit = TimeUnit.MINUTES)
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

        final SessionRecord record = buildSessionRecord(sessionProperties, now, expiresAt);
        sessionRepository.insert(record);

        final ImmutablePair<Token, Token> tokens = createTokens(record);
        final String serializedAccessToken = tokenSerializer.serializeToken(tokens.getLeft());
        final String serializedRefreshToken = tokenSerializer.serializeToken(tokens.getRight());

        return TokenSession.builder()
                .accessToken(serializedAccessToken)
                .refreshToken(serializedRefreshToken)
                .accessTokenTtl(jwtProperties.getAccessTtl())
                .refreshTokenTtl(jwtProperties.getRefreshTtl())
                .build();
    }

    /**
     * @return Pair of access token as the left value, and refresh token as the right value
     */
    @NotNull
    private ImmutablePair<Token, Token> createTokens(@NotNull SessionRecord record) {
        final Token accessToken = Token.builder()
                .subject(record.getId())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(record.getCreated().atZone(ZoneId.systemDefault()).toInstant())
                .expireAt(record.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant())
                .authorities(Collections.emptyList())
                .build();

        final Token refreshToken = accessToken.withExpireAt(accessToken.issuedAt().plus(jwtProperties.getRefreshTtl()))
                .withAuthorities(List.of(TokenAuthority.REFRESH));

        return ImmutablePair.of(accessToken, refreshToken);
    }

    @NotNull
    private SessionRecord buildSessionRecord(
            SessionProperties sessionProperties,
            LocalDateTime now,
            LocalDateTime expiresAt
    ) {
        final UUID sessionId = UUID.randomUUID();

        final SessionRecord record = new SessionRecord();
        record.setId(sessionId.toString());
        record.setClientIp(sessionProperties.requestDetails().clientIp());
        record.setUserAgent(sessionProperties.requestDetails().clientAgent());

        record.setUsername(sessionProperties.username());
        record.setStatus(SessionStatus.ACTIVE.name());

        record.setLastActive(now);
        record.setCreated(now);
        record.setUpdated(now);
        record.setExpiresAt(expiresAt);

        return record;
    }

}
