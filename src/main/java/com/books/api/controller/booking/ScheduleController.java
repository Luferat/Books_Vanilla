package com.books.api.controller.booking;

import com.books.api.model.Account;
import com.books.api.model.Schedule;
import com.books.api.repository.AccountRepository;
import com.books.api.service.ScheduleService;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<?> createSchedule(
            HttpServletRequest request,
            @RequestParam LocalDateTime scheduleDate,
            @RequestParam Integer durationDays,
            @RequestBody List<Long> bookIds
    ) {
        // Pegar usuário logado via token JWT
        Account loggedUser = jwtUtil.getLoggedUser(request, accountRepository);
        if (loggedUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("unauthorized", "Usuário não autenticado"));
        }

        try {
            Schedule schedule = scheduleService.createSchedule(
                    loggedUser.getId(),
                    scheduleDate,
                    durationDays,
                    bookIds
            );
            return ResponseEntity.ok(ApiResponse.success("schedule_created", "Agendamento criado com sucesso", schedule));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("schedule_error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("internal_error", "Erro interno no servidor"));
        }
    }
}
