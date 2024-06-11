package com.train.book.repository;

import com.train.book.entity.SeatEntity;
import com.train.book.entity.TicketEntity;
import com.train.book.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    Optional<SeatEntity> findBySectionAndSeatNumberAndStatus(@Param("section") String section, @Param("seatNumber") String seatNumber
    , @Param("status")Status status);
}
