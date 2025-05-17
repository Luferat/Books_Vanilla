package com.books.api.controller.booking;

import com.books.api.model.Account;
import com.books.api.model.Booking;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookingRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingCancelController {

    private final BookingRepository bookingRepository;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable Long id, HttpServletRequest request) {
        Account user = jwtUtil.getLoggedUser(request, accountRepository);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado"));
        }

        return bookingRepository.findById(id).map(b -> {
            b.setStatus(Booking.BookingStatus.CANCELED);
            bookingRepository.save(b);
            return ResponseEntity.ok(ApiResponse.success("200", "Agendamento cancelado", b));
        }).orElse(ResponseEntity.status(404).body(ApiResponse.error("404", "Agendamento não encontrado")));
    }
}
