package com.floweytech.agrotrack.iam.interfaces.rest.transform;


import com.floweytech.agrotrack.iam.domain.model.commands.SignInCommand;
import com.floweytech.agrotrack.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInresource) {
        return new SignInCommand(
                signInresource.identifier(),
                signInresource.password()
        );
    }
}
