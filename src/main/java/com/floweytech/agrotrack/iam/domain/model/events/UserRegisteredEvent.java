package com.floweytech.agrotrack.iam.domain.model.events;

public record UserRegisteredEvent(
        Long userId,
        String firstName,
        String lastName,
        String photoUrl
) {
}

