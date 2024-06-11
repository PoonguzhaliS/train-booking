package com.train.book.model.request;

import com.train.book.model.SeatModel;
import lombok.Data;

@Data
public class TicketRequest {
    private String userId;
    private SeatModel seatDetails;
    private String fromLocation;
    private String toLocation;
    private double price;
}
