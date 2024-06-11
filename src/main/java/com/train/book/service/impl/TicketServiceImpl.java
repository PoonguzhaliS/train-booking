package com.train.book.service.impl;


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
import com.train.book.service.TicketService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ModelMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TicketResponse bookTicket(TicketRequest request) {

        Optional<SeatEntity> existingSeat = seatRepository.findBySectionAndSeatNumberAndStatus(
                request.getSeatDetails().getSection(),
                request.getSeatDetails().getSeatNumber(),
                Status.BOOKED
        );
        if (existingSeat.isPresent()) {
            throw new CustomException("Seat is already booked.");
        }


        UserEntity user = userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> new RecordNotFoundException("User not found with id: " + request.getUserId()));


        SeatEntity seatEntity = new SeatEntity();
        seatEntity.setStatus(Status.BOOKED);
        seatEntity.setSeatNumber(request.getSeatDetails().getSeatNumber());
        seatEntity.setSection(request.getSeatDetails().getSection());
        seatEntity = seatRepository.save(seatEntity);


        TicketEntity ticket = new TicketEntity();
        ticket.setFromLocation(request.getFromLocation());
        ticket.setToLocation(request.getToLocation());
        ticket.setPrice(request.getPrice());
        ticket.setSeatEntity(seatEntity);
        ticket.setUser(user);

        TicketEntity ticketEntity = ticketRepository.save(ticket);


        if (user.getTickets() == null) {
            user.setTickets(new ArrayList<>());
        }
        List<TicketEntity> tickets = user.getTickets();
        tickets.add(ticketEntity);
        user.setTickets(tickets);
        userRepository.save(user);

        // Return the ticket response
        TicketResponse ticketResponse = mapper.map(ticketEntity, TicketResponse.class);
        SeatModel seatModel = new SeatModel();

        if(seatEntity != null) {
            seatModel.setSeatNumber(seatEntity.getSeatNumber());
            seatModel.setSection(seatEntity.getSection());
        }
        ticketResponse.setSeatDetails(seatModel);
        CreateUserResponse userResponse = new CreateUserResponse();
        if(user!=null) {
            userResponse.setEmail(user.getEmail());
            userResponse.setUserId(String.valueOf(user.getId()));
        }
        ticketResponse.setUser(userResponse);
        return ticketResponse;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TicketResponse> getTicketsBySection(String section) {
        List<TicketEntity> ticketEntityResponse = ticketRepository.findTicketsBySection(section);
        return ticketEntityResponse.stream().map(res -> mapper.map(res, TicketResponse.class)).toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public void cancelTicket(Long id) {
        Optional<TicketEntity> ticketEntity = ticketRepository.findById(id);
        if (!ticketEntity.isPresent()) {
            throw new RuntimeException("ticket is not available.");
        }
        TicketEntity ticket = ticketEntity.get();
        SeatEntity seatEntity = ticket.getSeatEntity();
        seatEntity.setStatus(Status.UNBOOKED);
        ticket.setSeatEntity(seatEntity);
        ticketRepository.save(ticket);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TicketResponse updateSeat(Long id, String newSection, String newSeatNumber) {
        try {
            TicketEntity ticket = ticketRepository.findByIdWithLock(id).orElseThrow(()->new RecordNotFoundException("ticket not found"));
            SeatEntity seatEntity = ticket.getSeatEntity();
            seatEntity.setSection(newSection);
            seatEntity.setSeatNumber(newSeatNumber);
            ticket.setSeatEntity(seatEntity);
            TicketEntity ticketEntity = ticketRepository.save(ticket);
            TicketResponse ticketResponse = mapper.map(ticketEntity, TicketResponse.class);
            SeatModel seatModel = new SeatModel();
            seatModel.setSeatNumber(ticketEntity.getSeatEntity().getSeatNumber());
            seatModel.setSection(ticketEntity.getSeatEntity().getSection());
            ticketResponse.setSeatDetails(seatModel);

            CreateUserResponse userResponse = new CreateUserResponse();
            userResponse.setEmail(ticketEntity.getUser().getEmail());
            userResponse.setUserId(String.valueOf(ticketEntity.getUser().getId()));
            ticketResponse.setUser(userResponse);
            return ticketResponse;
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update error. Please try again.");
        }
    }

}
