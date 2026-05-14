package com.floweytech.agrotrack.iam.infrastructure.hashing.bycript;

import com.floweytech.agrotrack.iam.application.internal.outboundedservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}

