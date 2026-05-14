package com.floweytech.agrotrack.iam.domain.model.commands;

public record SignInCommand(
        String identifier,
        String password
) {
}
