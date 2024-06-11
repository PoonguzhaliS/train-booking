package com.train.book.controller;

import com.train.book.model.request.CreateUserRequest;
import com.train.book.model.response.CreateUserResponse;
import com.train.book.model.response.UserResponse;
import com.train.book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("{emailId}")
    public ResponseEntity<UserResponse> getTicketInfoByEmailId(@PathVariable String emailId)
    {
        return ResponseEntity.ok(userService.getUserDetails(emailId));
    }

}
