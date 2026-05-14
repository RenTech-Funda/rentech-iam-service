package com.floweytech.agrotrack.iam.interfaces.rest.transform;


import com.floweytech.agrotrack.iam.domain.model.commands.SignUpCommand;
import com.floweytech.agrotrack.iam.interfaces.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource){
        return new SignUpCommand(
                resource.username(),
                resource.email(),
                resource.password(),
                resource.role(),
                resource.firstName(),
                resource.lastName(),
                resource.photoUrl()
        );
    }
}
