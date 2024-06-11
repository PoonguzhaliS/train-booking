package com.train.book.repository;

import com.train.book.entity.TicketEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TicketEntity t WHERE t.id = :id")
    Optional<TicketEntity> findByIdWithLock(@Param("id") Long id);
    @Query("SELECT t FROM TicketEntity t WHERE t.seatEntity.section = :section")
    List<TicketEntity> findTicketsBySection(@Param("section") String section);
}
