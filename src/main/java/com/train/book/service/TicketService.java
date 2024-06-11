package com.train.book.service;

import com.train.book.model.request.TicketRequest;
import com.train.book.model.response.TicketResponse;

import java.util.List;

public interface TicketService {
    TicketResponse bookTicket(TicketRequest request);
    List<TicketResponse> getTicketsBySection(String section);
    void deleteTicket(Long id);
    void cancelTicket(Long id);
    TicketResponse updateSeat(Long id, String newSection, String newSeatNumber);


}
