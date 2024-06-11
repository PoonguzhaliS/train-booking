package com.train.book.service;

import com.train.book.entity.SeatEntity;
import com.train.book.entity.TicketEntity;
import com.train.book.entity.UserEntity;
import com.train.book.exception.CustomException;
import com.train.book.exception.RecordNotFoundException;
import com.train.book.model.SeatModel;
import com.train.book.model.Status;
import com.train.book.model.request.TicketRequest;
import com.train.book.model.response.CreateUserResponse;
import com.train.book.model.response.TicketResponse;
import com.train.book.repository.SeatRepository;
import com.train.book.repository.TicketRepository;
import com.train.book.repository.UserRepository;
import com.train.book.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBookTicket() {
        // Arrange
        TicketRequest request = new TicketRequest();
        request.setUserId("1");
        request.setFromLocation("London");
        request.setToLocation("France");
        request.setPrice(5.0);
        SeatModel seatModel = new SeatModel();
        seatModel.setSection("A");
        seatModel.setSeatNumber("12");
        request.setSeatDetails(seatModel);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("john.doe@example.com");
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setUser(userEntity);
        ticketEntity.setFromLocation(request.getFromLocation());
        ticketEntity.setToLocation(request.getToLocation());
        ticketEntity.setPrice(request.getPrice());

        SeatEntity seatEntity = new SeatEntity();
        seatEntity.setSection(seatModel.getSection());
        seatEntity.setSeatNumber(seatModel.getSeatNumber());
        seatEntity.setStatus(Status.BOOKED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(seatRepository.findBySectionAndSeatNumberAndStatus(anyString(), anyString(), any(Status.class)))
                .thenReturn(Optional.empty());
        when(seatRepository.save(any(SeatEntity.class))).thenReturn(seatEntity);
        when(ticketRepository.save(any(TicketEntity.class))).thenReturn(ticketEntity);
        when(mapper.map(any(TicketEntity.class), eq(TicketResponse.class))).thenReturn(new TicketResponse());

        // Act
        TicketResponse response = ticketService.bookTicket(request);

        // Assert
        assertNotNull(response);
    }

    @Test
    public void testGetTicketsBySection() {
        // Arrange
        List<TicketEntity> ticketEntities = new ArrayList<>();
        when(ticketRepository.findTicketsBySection(anyString())).thenReturn(ticketEntities);
        when(mapper.map(any(TicketEntity.class), eq(TicketResponse.class))).thenReturn(new TicketResponse());

        // Act
        List<TicketResponse> responses = ticketService.getTicketsBySection("A");

        // Assert
        assertNotNull(responses);
    }

    @Test
    public void testCancelTicket() {
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setId(1L);
        SeatEntity seatEntity = new SeatEntity();
        seatEntity.setStatus(Status.BOOKED);
        ticketEntity.setSeatEntity(seatEntity);
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticketEntity));
        when(ticketRepository.save(any(TicketEntity.class))).thenReturn(ticketEntity);

        ticketService.cancelTicket(1L);

        assertEquals(Status.UNBOOKED, seatEntity.getStatus());
    }

    @Test
    public void testCancelTicket_whenTicketNotFound() {
        // Arrange
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ticketService.cancelTicket(1L));
    }

    @Test
    public void testUpdateSeat() {
        // Arrange
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setId(1L);
        SeatEntity seatEntity = new SeatEntity();
        seatEntity.setSection("A");
        seatEntity.setSeatNumber("12");
        ticketEntity.setSeatEntity(seatEntity);
        UserEntity userEntity=new UserEntity();
        userEntity.setFirstName("john");
        userEntity.setEmail("john@test.com");
        userEntity.setId(1l);
        ticketEntity.setUser(userEntity);


        when(ticketRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(ticketEntity));
        when(ticketRepository.save(any(TicketEntity.class))).thenReturn(ticketEntity);
        when(mapper.map(any(TicketEntity.class), eq(TicketResponse.class))).thenReturn(new TicketResponse());

        // Act
        TicketResponse response = ticketService.updateSeat(1L, "B", "20");

        // Assert
        assertNotNull(response);
        assertEquals("B", response.getSeatDetails().getSection());
        assertEquals("20", response.getSeatDetails().getSeatNumber());
    }

    @Test
    public void testUpdateSeat_whenTicketNotFound() {
        // Arrange
        when(ticketRepository.findByIdWithLock(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> ticketService.updateSeat(1L, "B", "20"));
    }

    @Test
    public void testUpdateSeat_whenConcurrencyIssue() {
        // Arrange
        when(ticketRepository.findByIdWithLock(anyLong())).thenThrow(OptimisticLockingFailureException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ticketService.updateSeat(1L, "B", "20"));
    }
}
