package com.train.book.controller;

import com.train.book.model.request.SeatChangeRequest;
import com.train.book.model.request.TicketRequest;
import com.train.book.model.response.TicketResponse;
import com.train.book.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/book")
    public ResponseEntity<TicketResponse> bookTicket(@RequestBody TicketRequest request) {

        return ResponseEntity.ok(ticketService.bookTicket(request));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelTicket(@PathVariable Long id) {

         ticketService.cancelTicket(id);
       return ResponseEntity.ok("Canceled successfully");
    }

    @GetMapping("/section/{section}")
    public ResponseEntity<List<TicketResponse>> getTicketsBySection(@PathVariable String section) {
        return ResponseEntity.ok(ticketService.getTicketsBySection(section));
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable Long id) {

        ticketService.deleteTicket(id);
        ResponseEntity.ok("deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateSeat(@PathVariable Long id, @RequestBody SeatChangeRequest request) {
        return ResponseEntity.ok(ticketService.updateSeat(id, request.getNewSection(), request.getNewSeatNumber()));
    }
}
