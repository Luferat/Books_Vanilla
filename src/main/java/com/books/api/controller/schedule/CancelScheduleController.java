package com.books.api.controller.schedule;

import com.books.api.model.Account;
import com.books.api.model.Schedule;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.ScheduleRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class CancelScheduleController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final ScheduleRepository scheduleRepository;

    @PostMapping("/cancel/{id}")
    @Transactional // Garante que a operação seja atômica
    public ResponseEntity<?> cancelSchedule(@PathVariable Long id, HttpServletRequest httpRequest) {
        // 1. Autenticação e Autorização do Usuário
        Account loggedUser = jwtUtil.getLoggedUser(httpRequest, accountRepository);
        if (loggedUser == null || loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado ou inativo."));
        }

        // 2. Buscar o Agendamento pelo ID
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);
        if (scheduleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Agendamento não encontrado."));
        }

        Schedule schedule = scheduleOptional.get();

        // 3. Verificar se o agendamento pertence ao usuário logado
        if (!schedule.getAccount().getId().equals(loggedUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("403", "Acesso negado. Este agendamento não pertence ao usuário logado."));
        }

        // 4. Verificar se a scheduleDate está no futuro
        if (schedule.getScheduleDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Não é possível cancelar um agendamento com data passada."));
        }

        // 5. Verificar se o agendamento já está cancelado
        if (schedule.getStatus() == Schedule.Status.CANCELED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Este agendamento já está cancelado."));
        }

        // 6. Atualizar o Status do Agendamento para CANCELED
        schedule.setStatus(Schedule.Status.CANCELED);
        scheduleRepository.save(schedule);

        // TODO: Em um sistema real, aqui você também deveria reverter o estoque dos livros
        // associados a este agendamento cancelado. Isso não foi solicitado, mas é uma consideração importante.

        // 7. Retornar Resposta de Sucesso
        return ResponseEntity.ok(ApiResponse.success("200", "Agendamento cancelado com sucesso."));
    }
}
