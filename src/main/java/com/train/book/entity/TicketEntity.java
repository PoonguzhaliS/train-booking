package com.train.book.entity;

import com.train.book.model.SeatModel;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fromLocation;
    private String toLocation;
    private double price;
    @OneToOne
    private SeatEntity seatEntity;
    @ManyToOne
    private UserEntity user;
}
