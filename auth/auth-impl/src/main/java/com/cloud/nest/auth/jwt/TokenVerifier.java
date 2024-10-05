package com.cloud.nest.auth.jwt;

import com.cloud.nest.auth.model.AccessToken;
import com.cloud.nest.auth.model.RefreshToken;

public interface TokenVerifier {
    AccessToken verifyAccessToken(String accessToken);

    RefreshToken verifyRefreshToken(String refreshToken);
}
