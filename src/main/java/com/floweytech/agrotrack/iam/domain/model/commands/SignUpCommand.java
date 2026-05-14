package com.floweytech.agrotrack.iam.domain.model.commands;
import com.floweytech.agrotrack.iam.domain.model.valueobjects.Roles;

public record SignUpCommand(
   String username,
   String email,
   String password,
   Roles role,
   String firstName,
   String lastName,
   String photoUrl
) {}
