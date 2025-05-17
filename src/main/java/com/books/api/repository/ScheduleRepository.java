package com.books.api.repository;

import com.books.api.model.Book;
import com.books.api.model.Schedule;
import com.books.api.model.Schedule.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Schedule s JOIN s.books b WHERE b = :book AND s.status = :status AND s.scheduleDate BETWEEN :startDate AND :endDate")
    boolean existsByBookAndStatusAndScheduleDateBetween(Book book, Status status, LocalDateTime startDate, LocalDateTime endDate);

}
