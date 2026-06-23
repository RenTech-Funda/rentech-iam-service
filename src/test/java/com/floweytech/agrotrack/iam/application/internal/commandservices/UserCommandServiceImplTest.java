package com.floweytech.agrotrack.iam.application.internal.commandservices;

import com.floweytech.agrotrack.iam.application.internal.outboundedservices.hashing.HashingService;
import com.floweytech.agrotrack.iam.application.internal.outboundedservices.tokens.TokenService;
import com.floweytech.agrotrack.iam.domain.model.aggregates.User;
import com.floweytech.agrotrack.iam.domain.model.commands.SignInCommand;
import com.floweytech.agrotrack.iam.domain.model.commands.SignUpCommand;
import com.floweytech.agrotrack.iam.domain.model.events.UserRegisteredEvent;
import com.floweytech.agrotrack.iam.domain.model.valueobjects.Roles;
import com.floweytech.agrotrack.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;


    @Test
    @DisplayName("SignIn exitoso con identificador por email")
    void handleSignInSuccessWithEmail() {
        // Arrange
        SignInCommand command = new SignInCommand("diego@agrotrack.com", "password123");
        User mockUser = new User("diego", "diego@agrotrack.com", "hashedPassword", Roles.ROLE_FARMER);

        when(userRepository.findByEmail(command.identifier())).thenReturn(Optional.of(mockUser));
        when(hashingService.matches(command.password(), mockUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(mockUser.getUsername(), mockUser.getRole().name(), mockUser.getId()))
                .thenReturn("mock-jwt-token");

        // Act
        Optional<ImmutablePair<User, String>> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("diego", result.get().getLeft().getUsername());
        assertEquals("mock-jwt-token", result.get().getRight());
    }

    @Test
    @DisplayName("SignIn falla si la contraseña es incorrecta")
    void handleSignInFailsWhenPasswordDoesNotMatch() {
        // Arrange
        SignInCommand command = new SignInCommand("diego", "wrongPassword");
        User mockUser = new User("diego", "diego@agrotrack.com", "hashedPassword", Roles.ROLE_FARMER);

        when(userRepository.findByUsername(command.identifier())).thenReturn(Optional.of(mockUser));
        when(hashingService.matches(command.password(), mockUser.getPassword())).thenReturn(false);

        // Act
        Optional<ImmutablePair<User, String>> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isEmpty());
        verify(tokenService, never()).generateToken(any(), any(), any());
    }
    

    @Test
    @DisplayName("SignUp exitoso genera el usuario y publica evento en RabbitMQ")
    void handleSignUpSuccessAndPublishEvent() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "crispin", "crispin@agrotrack.com", "pass123", Roles.ROLE_AGRONOMIST,
                "Daniel", "Crispin", "http://photo.com/daniel"
        );
        User savedUser = new User("crispin", "crispin@agrotrack.com", "encodedPass", Roles.ROLE_AGRONOMIST);

        when(userRepository.existsByUsername(command.username())).thenReturn(false);
        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(hashingService.encode(command.password())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("crispin", result.get().getUsername());

        // Verificar el patrón Transactional Outbox simulado (envío a RabbitMQ)
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("agrotrack.exchange"),
                eq("user.registered"),
                any(UserRegisteredEvent.class)
        );
    }

    @Test
    @DisplayName("SignUp lanza excepción si el Username ya existe")
    void handleSignUpThrowsExceptionWhenUsernameExists() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "crispin", "crispin@agrotrack.com", "pass123", Roles.ROLE_AGRONOMIST,
                "Daniel", "Crispin", "http://photo.com/daniel"
        );
        when(userRepository.existsByUsername(command.username())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userCommandService.handle(command);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }
}