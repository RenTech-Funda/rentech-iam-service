package com.floweytech.agrotrack.iam.infrastructure.tokens.jwt;

import com.floweytech.agrotrack.iam.application.internal.outboundedservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;

public interface BearerTokenService extends TokenService {
    String getBearerTokenFrom(HttpServletRequest token);
}