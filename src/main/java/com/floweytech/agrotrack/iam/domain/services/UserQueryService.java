package com.floweytech.agrotrack.iam.domain.services;

import com.floweytech.agrotrack.iam.domain.model.aggregates.User;
import com.floweytech.agrotrack.iam.domain.model.queries.GetAllUsersQuery;
import com.floweytech.agrotrack.iam.domain.model.queries.GetUserByEmailQuery;
import com.floweytech.agrotrack.iam.domain.model.queries.GetUserByIdQuery;
import com.floweytech.agrotrack.iam.domain.model.queries.GetUserByUsernameQuery;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    List<User> handle(GetAllUsersQuery query);
    Optional<User> handle(GetUserByEmailQuery query);
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByUsernameQuery query);
}