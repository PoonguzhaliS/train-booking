package com.train.book.service;

import com.train.book.model.request.CreateUserRequest;
import com.train.book.model.response.CreateUserResponse;
import com.train.book.model.response.UserResponse;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);

    UserResponse getUserDetails(String emailId);
}
