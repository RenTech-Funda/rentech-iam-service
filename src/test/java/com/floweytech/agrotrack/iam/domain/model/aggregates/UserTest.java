package com.floweytech.agrotrack.iam.domain.model.aggregates;

import com.floweytech.agrotrack.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Debería requerir login por email si el rol es AGRONOMIST o FARMER")
    void shouldRequireEmailLoginWhenRoleIsAgronomistOrFarmer() {
        // Arrange & Act
        User agronomist = new User("agro1", "agro@test.com", "password", Roles.ROLE_AGRONOMIST);
        User farmer = new User("farmer1", "farmer@test.com", "password", Roles.ROLE_FARMER);

        // Assert
        assertTrue(agronomist.isEmailLoginRequired());
        assertTrue(farmer.isEmailLoginRequired());
    }

    @Test
    @DisplayName("No debería requerir login por email si el rol no es Agrónomo ni Agricultor (ej. un Admin)")
    void shouldNotRequireEmailLoginWhenRoleIsDifferent() {
        User user = new User("admin1", "admin@test.com", "password", Roles.ROLE_SUPER_ADMIN);

        assertFalse(user.isEmailLoginRequired());
    }
}