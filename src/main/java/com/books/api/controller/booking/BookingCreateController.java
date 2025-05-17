package com.books.api.controller.booking;

import com.books.api.model.Account;
import com.books.api.model.Booking;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookingRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingCreateController {

    private final BookingRepository bookingRepository;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> schedule(@RequestBody Booking booking, HttpServletRequest request) {
        Account user = jwtUtil.getLoggedUser(request, accountRepository);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado"));
        }


        boolean isBooked = bookingRepository.existsByBookTitleAndStatus(
                booking.getBookTitle(),
                Booking.BookingStatus.SCHEDULED
                //booking.getBookingDate()
        );

        // Verifica se o livro já está agendado para o dia desejado
        if (isBooked) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("409", "Livro já está agendado para essa data"));
        }

        booking.setBookingDate(LocalDateTime.now()); // data do agendamento atual
        booking.setStatus(Booking.BookingStatus.SCHEDULED);
        Booking saved = bookingRepository.save(booking);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Agendamento realizado com sucesso", saved));
    }
}
