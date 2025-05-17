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

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingUpdateController {

    private final BookingRepository bookingRepository;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Booking newData, HttpServletRequest request) {
        Account user = jwtUtil.getLoggedUser(request, accountRepository);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado"));
        }

        return bookingRepository.findById(id).map(b -> {
            b.setCustomerName(newData.getCustomerName());
            b.setBookTitle(newData.getBookTitle());
            b.setStatus(Booking.BookingStatus.UPDATED);
            Booking updated = bookingRepository.save(b);
            return ResponseEntity.ok(ApiResponse.success("200", "Agendamento atualizado", updated));
        }).orElse(ResponseEntity.status(404).body(ApiResponse.error("404", "Agendamento não encontrado")));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Map<String, Object>> markAsReturned(@PathVariable Long id, HttpServletRequest request) {
        Account user = jwtUtil.getLoggedUser(request, accountRepository);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado"));
        }

        return bookingRepository.findById(id).map(b -> {
            b.setReturnDate(LocalDateTime.now());
            b.setStatus(Booking.BookingStatus.RETURNED);
            Booking updated = bookingRepository.save(b);
            return ResponseEntity.ok(ApiResponse.success("200", "Livro marcado como devolvido", updated));
        }).orElse(ResponseEntity.status(404).body(ApiResponse.error("404", "Agendamento não encontrado")));
    }
}
