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
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ListScheduleController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final ScheduleRepository scheduleRepository;

    @GetMapping("/list")
    public ResponseEntity<?> listMySchedules(HttpServletRequest httpRequest) {
        // 1. Autenticação e Autorização do Usuário
        Account loggedUser = jwtUtil.getLoggedUser(httpRequest, accountRepository);
        if (loggedUser == null || loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado ou inativo."));
        }

        // 2. Buscar Agendamentos para o usuário logado usando a consulta customizada
        List<Schedule> schedules = scheduleRepository.findByAccountWithDetails(loggedUser);

        // 3. Lidar com o caso de nenhum agendamento encontrado
        if (schedules.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Nenhum agendamento encontrado para este usuário."));
        }

        // 4. Mapear Agendamentos para a estrutura JSON desejada
        List<Map<String, Object>> responseList = schedules.stream().map(schedule -> {
            Map<String, Object> scheduleMap = new HashMap<>();
            scheduleMap.put("id", schedule.getId());
            scheduleMap.put("createdAt", schedule.getCreatedAt());
            scheduleMap.put("scheduleDate", schedule.getScheduleDate());
            scheduleMap.put("durationDays", schedule.getDurationDays());
            scheduleMap.put("status", schedule.getStatus().name());

            // Informações da Conta (ID e Nome) - já carregadas eagermente
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("id", schedule.getAccount().getId());
            accountInfo.put("name", schedule.getAccount().getName());
            scheduleMap.put("account", accountInfo);

            // Itens do Agendamento (Livros) - já carregados eagermente
            List<Map<String, Object>> bookItems = schedule.getItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("bookId", item.getBook().getId());
                itemMap.put("bookTitle", item.getBook().getTitle());
                // Adicione outros detalhes do livro se necessário aqui
                return itemMap;
            }).collect(Collectors.toList());
            scheduleMap.put("books", bookItems); // Renomeado 'items' para 'books' para clareza na resposta

            return scheduleMap;
        }).collect(Collectors.toList());

        // 5. Retornar Resposta de Sucesso
        return ResponseEntity.ok(
                ApiResponse.success("200", "Agendamentos carregados com sucesso.", responseList)
        );
    }
}
