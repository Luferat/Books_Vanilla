package com.books.api.controller.schedule;

import com.books.api.model.Account;
import com.books.api.model.Schedule;
import com.books.api.model.ScheduleItem;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.ScheduleRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            BigDecimal currentScheduleTotalRentalPrice = BigDecimal.ZERO; // Inicializa o preço total para este agendamento
            List<Map<String, Object>> bookItems = schedule.getItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("bookId", item.getBook().getId());
                itemMap.put("bookTitle", item.getBook().getTitle());
                itemMap.put("bookPrice", item.getBook().getPrice()); // Adiciona o preço individual do livro no item

                // Calcula o preço do aluguel para este item e adiciona ao total do agendamento
                BigDecimal itemRentalPrice = item.getBook().getPrice().multiply(BigDecimal.valueOf(schedule.getDurationDays()));
                itemMap.put("itemRentalPrice", itemRentalPrice); // Adiciona o preço do aluguel para este item

                // Soma ao total do agendamento
                // É importante que currentScheduleTotalRentalPrice seja final ou efetivamente final para ser usado em lambda,
                // ou que seja um AtomicReference, ou que o cálculo seja feito após o stream.
                // A forma mais direta aqui é acumular fora do stream e depois adicionar ao mapa.
                // Vamos refatorar para acumular antes do stream de items.
                return itemMap;
            }).collect(Collectors.toList());
            scheduleMap.put("books", bookItems); // Renomeado 'items' para 'books' para clareza na resposta

            // Recalcular o total fora do stream para garantir a acumulação correta
            BigDecimal calculatedTotalRentalPrice = BigDecimal.ZERO;
            for (ScheduleItem item : schedule.getItems()) {
                calculatedTotalRentalPrice = calculatedTotalRentalPrice.add(
                        item.getBook().getPrice().multiply(BigDecimal.valueOf(schedule.getDurationDays()))
                );
            }
            scheduleMap.put("totalRentalPrice", calculatedTotalRentalPrice); // Adiciona o preço total do aluguel para este agendamento

            return scheduleMap;
        }).collect(Collectors.toList());

        // 5. Retornar Resposta de Sucesso
        return ResponseEntity.ok(
                ApiResponse.success("200", "Agendamentos carregados com sucesso.", responseList)
        );
    }
}
