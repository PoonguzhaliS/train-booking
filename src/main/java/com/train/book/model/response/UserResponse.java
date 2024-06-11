package com.train.book.model.response;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String email;

    private List<TicketResponse> ticket;
}
