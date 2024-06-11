package com.train.book.service.impl;

import com.train.book.entity.TicketEntity;
import com.train.book.entity.UserEntity;
import com.train.book.exception.RecordNotFoundException;
import com.train.book.model.request.CreateUserRequest;
import com.train.book.model.response.CreateUserResponse;
import com.train.book.model.response.TicketResponse;
import com.train.book.model.response.UserResponse;
import com.train.book.repository.UserRepository;
import com.train.book.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;
    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        UserEntity userEntity = mapper.map(request, UserEntity.class);
        UserEntity response=userRepository.save(userEntity);
       return mapper.map(response, CreateUserResponse.class);
    }

    @Override
    public UserResponse getUserDetails(String emailId) {
        Optional<UserEntity> byEmailId = userRepository.findByEmail(emailId);
        if(!byEmailId.isPresent())
        {
            throw new RecordNotFoundException("user is not available");
        }

        List<TicketEntity> tickets = byEmailId.get().getTickets();
        List<TicketResponse> ticketResponseList=null;
        if (tickets != null || !tickets.isEmpty()) {
            ticketResponseList =
                    tickets.stream().map(ticket -> mapper.map(ticket, TicketResponse.class)).collect(Collectors.toList());

        }
        UserResponse response=new UserResponse();
        response.setId(byEmailId.get().getId());
        response.setEmail(byEmailId.get().getEmail());
        response.setTicket(ticketResponseList);

        return response;
    }
}
