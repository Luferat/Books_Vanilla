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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ListOneScheduleController {

    // Id de usuário para experimentos
    int loggedUserId = 1;

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final ScheduleRepository scheduleRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long id, HttpServletRequest httpRequest) {

        /** Desabilitado para experimentos
        // 1. Autenticação e Autorização do Usuário
        Account loggedUser = jwtUtil.getLoggedUser(httpRequest, accountRepository);
        if (loggedUser == null || loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado ou inativo."));
        }
        **/

        // 2. Buscar o Agendamento pelo ID
        // Usamos findById que já deve carregar as relações eagermente devido à consulta em ScheduleRepository,
        // mas para garantir, podemos ter um método específico se findById não for suficiente.
        // Considerando que findByAccountWithDetails já faz um JOIN FETCH, podemos adaptar.
        // Para um único agendamento, findById é o padrão, mas precisamos verificar a propriedade do usuário.
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);

        // 3. Lidar com o caso de agendamento não encontrado
        if (scheduleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Agendamento não encontrado."));
        }

        Schedule schedule = scheduleOptional.get();

        // 4. Verificar se o agendamento pertence ao usuário logado
        // if (!schedule.getAccount().getId().equals(loggedUser.getId())) { // Desabilitado para experimentos
        if (!schedule.getAccount().getId().equals(loggedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("403", "Acesso negado. Este agendamento não pertence ao usuário logado."));
        }

        // 5. Mapear o Agendamento para a estrutura JSON desejada e calcular o preço total
        Map<String, Object> scheduleMap = new HashMap<>();
        scheduleMap.put("id", schedule.getId());
        scheduleMap.put("createdAt", schedule.getCreatedAt());
        scheduleMap.put("scheduleDate", schedule.getScheduleDate());
        scheduleMap.put("durationDays", schedule.getDurationDays());
        scheduleMap.put("status", schedule.getStatus().name());

        // Informações da Conta (ID e Nome)
        Map<String, Object> accountInfo = new HashMap<>();
        accountInfo.put("id", schedule.getAccount().getId());
        accountInfo.put("name", schedule.getAccount().getName());
        scheduleMap.put("account", accountInfo);

        // Itens do Agendamento (Livros) e cálculo do preço total
        BigDecimal calculatedTotalRentalPrice = BigDecimal.ZERO;
        List<Map<String, Object>> bookItems = new ArrayList<>();

        // Garante que a lista de itens não seja nula antes de iterar
        if (schedule.getItems() != null) {
            for (ScheduleItem item : schedule.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("bookId", item.getBook().getId());
                itemMap.put("bookTitle", item.getBook().getTitle());
                itemMap.put("bookPrice", item.getBook().getPrice());

                BigDecimal itemRentalPrice = item.getBook().getPrice().multiply(BigDecimal.valueOf(schedule.getDurationDays()));
                itemMap.put("itemRentalPrice", itemRentalPrice);

                bookItems.add(itemMap);
                calculatedTotalRentalPrice = calculatedTotalRentalPrice.add(itemRentalPrice);
            }
        }
        scheduleMap.put("books", bookItems);
        scheduleMap.put("totalRentalPrice", calculatedTotalRentalPrice);

        // 6. Retornar Resposta de Sucesso
        return ResponseEntity.ok(
                ApiResponse.success("200", "Agendamento carregado com sucesso.", scheduleMap)
        );
    }
}
