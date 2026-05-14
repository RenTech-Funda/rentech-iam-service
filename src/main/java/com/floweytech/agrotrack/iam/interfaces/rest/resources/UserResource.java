package com.floweytech.agrotrack.iam.interfaces.rest.resources;

import com.floweytech.agrotrack.iam.domain.model.valueobjects.Roles;

public record UserResource(
        Long id,
        String username,
        Roles role
        ) {
}
