package com.train.book.controller;

import com.train.book.model.request.SeatChangeRequest;
import com.train.book.model.request.TicketRequest;
import com.train.book.model.response.TicketResponse;
import com.train.book.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TicketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    public void testBookTicket() throws Exception {
        TicketRequest ticketRequest = new TicketRequest();
        TicketResponse ticketResponse = new TicketResponse();

        when(ticketService.bookTicket(any(TicketRequest.class))).thenReturn(ticketResponse);

        mockMvc.perform(post("/v1/tickets/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"1\",\"seatDetails\":{\"section\":\"A\",\"seatNumber\":\"12\"},\"fromLocation\":\"London\",\"toLocation\":\"France\",\"price\":5.0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(ticketService, times(1)).bookTicket(any(TicketRequest.class));
    }

    @Test
    public void testCancelTicket() throws Exception {
        doNothing().when(ticketService).cancelTicket(anyLong());

        mockMvc.perform(put("/v1/tickets/cancel/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Canceled successfully"));

        verify(ticketService, times(1)).cancelTicket(anyLong());
    }

    @Test
    public void testGetTicketsBySection() throws Exception {
        TicketResponse ticketResponse = new TicketResponse();
        when(ticketService.getTicketsBySection(anyString())).thenReturn(Collections.singletonList(ticketResponse));

        mockMvc.perform(get("/v1/tickets/section/A"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(ticketService, times(1)).getTicketsBySection(anyString());
    }


    @Test
    public void testUpdateSeat() throws Exception {
        SeatChangeRequest seatChangeRequest = new SeatChangeRequest();
        seatChangeRequest.setNewSection("B");
        seatChangeRequest.setNewSeatNumber("20");
        TicketResponse ticketResponse = new TicketResponse();

        when(ticketService.updateSeat(anyLong(), anyString(), anyString())).thenReturn(ticketResponse);

        mockMvc.perform(put("/v1/tickets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newSection\":\"B\",\"newSeatNumber\":\"20\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(ticketService, times(1)).updateSeat(anyLong(), anyString(), anyString());
    }
}
