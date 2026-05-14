package com.floweytech.agrotrack.iam.application.internal.queryservices;

import com.floweytech.agrotrack.iam.domain.model.aggregates.User;
import com.floweytech.agrotrack.iam.domain.model.queries.GetAllUsersQuery;
import com.floweytech.agrotrack.iam.domain.model.queries.GetUserByEmailQuery;
import com.floweytech.agrotrack.iam.domain.model.queries.GetUserByIdQuery;
import com.floweytech.agrotrack.iam.domain.model.queries.GetUserByUsernameQuery;
import com.floweytech.agrotrack.iam.domain.services.UserQueryService;
import com.floweytech.agrotrack.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserQueryServicesImpl implements UserQueryService {
    private final UserRepository userRepository;
    public UserQueryServicesImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByUsernameQuery query) {
        return userRepository.findByUsername(query.username());
    }
}
