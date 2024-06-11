package com.train.book.model.request;

import lombok.Data;

@Data
public class SeatChangeRequest {
    private String newSection;
    private String newSeatNumber;
}
