package com.books.api.controller.booking;

import com.books.api.model.Booking;
import com.books.api.repository.BookingRepository;
import com.books.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingReadController {

    private final BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listAll() {
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("200", "Lista de agendamentos", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(b -> ResponseEntity.ok(ApiResponse.success("200", "Agendamento encontrado", b)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("404", "Agendamento não encontrado")));
    }
}
