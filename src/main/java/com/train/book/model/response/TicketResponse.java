package com.train.book.model.response;

import com.train.book.entity.UserEntity;
import com.train.book.model.SeatModel;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class TicketResponse {
    private Long id;
    private String fromLocation;
    private String toLocation;
    private double price;
    private SeatModel seatDetails;
    private CreateUserResponse user;
}
