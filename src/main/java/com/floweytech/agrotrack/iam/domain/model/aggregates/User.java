package com.floweytech.agrotrack.iam.domain.model.aggregates;

import com.floweytech.agrotrack.iam.domain.model.valueobjects.Roles;
import com.floweytech.agrotrack.iam.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @Email
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false)
    private String password;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private Roles role;

    @Setter
    @Column(nullable = false)
    private boolean twoFactorEnabled = false;

    @Setter
    @Column(nullable = false)
    private boolean active = true;

    public boolean isEmailLoginRequired(){
        return role == Roles.ROLE_AGRONOMIST || role == Roles.ROLE_FARMER;
    }

    public User(String username, String email, String password, Roles role){
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(){
    }

}