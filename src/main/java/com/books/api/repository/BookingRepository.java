package com.books.api.repository;

import com.books.api.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Busca todos os agendamentos pelo título do livro e data do agendamento (apenas data, sem horário).
     * Isso permite verificar se o livro já está agendado naquele dia.
     */
    List<Booking> findByBookTitleAndBookingDate(String bookTitle, LocalDate bookingDate);

    /**
     * Busca agendamentos apenas pelo título do livro.
     * Método opcional, pode ser útil para outros casos.
     */
    List<Booking> findByBookTitle(String bookTitle);

    boolean existsByBookTitleAndStatus(String bookTitle, Booking.BookingStatus bookingStatus);
}
