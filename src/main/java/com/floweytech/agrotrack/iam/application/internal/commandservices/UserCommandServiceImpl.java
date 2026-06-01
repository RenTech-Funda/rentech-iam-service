package com.floweytech.agrotrack.iam.application.internal.commandservices;


import com.floweytech.agrotrack.iam.application.internal.outboundedservices.hashing.HashingService;
import com.floweytech.agrotrack.iam.application.internal.outboundedservices.tokens.TokenService;
import com.floweytech.agrotrack.iam.domain.model.aggregates.User;
import com.floweytech.agrotrack.iam.domain.model.commands.SignInCommand;
import com.floweytech.agrotrack.iam.domain.model.commands.SignUpCommand;
import com.floweytech.agrotrack.iam.domain.model.events.UserRegisteredEvent;
import com.floweytech.agrotrack.iam.domain.services.UserCommandService;
import com.floweytech.agrotrack.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RabbitTemplate rabbitTemplate;

    private UserCommandServiceImpl(UserRepository userRepository,
                                   HashingService hashingService,
                                   TokenService tokenService,
                                   RabbitTemplate rabbitTemplate){
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        Optional<User> userOpt;

        if (command.identifier().contains("@")) {
            userOpt = userRepository.findByEmail(command.identifier());
        } else {
            userOpt = userRepository.findByUsername(command.identifier());
        }
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();

        if (!hashingService.matches(command.password(), user.getPassword())) {
            return Optional.empty();
        }
        String token = tokenService.generateToken(user.getUsername(), user.getRole().name(), user.getId());

        return Optional.of(ImmutablePair.of(user, token));
    }


    @Override
    public Optional<User> handle(SignUpCommand command) {
        if(userRepository.existsByUsername(command.username()))
            throw new RuntimeException("Username already exists");
        if (command.email() != null && userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("Email already exists");
        }
        var user = new User(
                command.username(),
                command.email(),
                hashingService.encode(command.password()),
                command.role());
        var savedUser = userRepository.save(user);

        // Emit event to create profile
        var event = new UserRegisteredEvent(
                savedUser.getId(),
                command.firstName(),
                command.lastName(),
                command.photoUrl()
        );
        
        // Parámetros: (Nombre_del_Exchange, Routing_Key, Evento_a_enviar)
        rabbitTemplate.convertAndSend("agrotrack.exchange", "user.registered", event);

        return Optional.of(savedUser);
    }

}
