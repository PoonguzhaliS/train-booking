package com.train.book.service;

import com.train.book.entity.TicketEntity;
import com.train.book.entity.UserEntity;
import com.train.book.exception.RecordNotFoundException;
import com.train.book.model.request.CreateUserRequest;
import com.train.book.model.response.CreateUserResponse;
import com.train.book.model.response.TicketResponse;
import com.train.book.model.response.UserResponse;
import com.train.book.repository.UserRepository;
import com.train.book.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    public void testGetUserDetails() {
        String emailId = "john.doe@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(emailId);
        List<TicketEntity> tickets = new ArrayList<>();
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setId(1L);
        tickets.add(ticketEntity);
        userEntity.setTickets(tickets);
        when(userRepository.findByEmail(emailId)).thenReturn(Optional.of(userEntity));
        when(mapper.map(ticketEntity, TicketResponse.class)).thenReturn(new TicketResponse());


        UserResponse response = userService.getUserDetails(emailId);


        assertNotNull(response);
        assertEquals(userEntity.getId(), response.getId());
        assertEquals(userEntity.getEmail(), response.getEmail());
        assertFalse(response.getTicket().isEmpty());
    }

    @Test
    public void testGetUserDetails_whenUserNotFound() {

        String emailId = "john.doe@example.com";
        when(userRepository.findByEmail(emailId)).thenReturn(Optional.empty());


        assertThrows(RecordNotFoundException.class, () -> userService.getUserDetails(emailId));
    }
}
