package com.cloud.nest.auth.jwt;

import com.cloud.nest.auth.model.Token;

public interface TokenVerifier {
    Token verifyAccessToken(String accessToken);
}
