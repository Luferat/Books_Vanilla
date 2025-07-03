package com.books.api.controller.schedule;

import com.books.api.model.Account;
import com.books.api.model.Book;
import com.books.api.model.Schedule;
import com.books.api.model.ScheduleItem;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.repository.ScheduleRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class NewScheduleController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;
    private final ScheduleRepository scheduleRepository;

    @PostMapping("/create")
    @Transactional // Garante que a operação seja atômica (ou tudo salva, ou nada)
    public ResponseEntity<?> createSchedule(@RequestBody Map<String, Object> requestBody, HttpServletRequest httpRequest) {

        // 1. Autenticação e Autorização do Usuário
        Account loggedUser = jwtUtil.getLoggedUser(httpRequest, accountRepository);
        if (loggedUser == null || loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado ou inativo."));
        }

        // 2. Extrair e Validar Dados da Requisição (Manual)
        LocalDateTime scheduleDate;
        Integer durationDays;
        List<Long> bookIds;

        // scheduleDate
        Object scheduleDateObj = requestBody.get("scheduleDate");
        if (scheduleDateObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A data do agendamento é obrigatória."));
        }
        try {
            scheduleDate = LocalDateTime.parse(scheduleDateObj.toString());
            if (scheduleDate.isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "A data do agendamento não pode ser no passado."));
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Formato de data do agendamento inválido. Use yyyy-MM-dd'T'HH:mm:ss."));
        }

        // durationDays
        Object durationDaysObj = requestBody.get("durationDays");
        if (durationDaysObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A duração em dias é obrigatória."));
        }
        try {
            // Jackson pode desserializar números como Integer por padrão se couberem
            // Se for um número maior que Integer.MAX_VALUE, pode vir como Long
            if (durationDaysObj instanceof Integer) {
                durationDays = (Integer) durationDaysObj;
            } else if (durationDaysObj instanceof Long) {
                durationDays = ((Long) durationDaysObj).intValue();
            } else {
                throw new ClassCastException();
            }

            if (durationDays < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "A duração deve ser de pelo menos 1 dia."));
            }
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Duração em dias deve ser um número inteiro."));
        }

        // bookIds
        Object bookIdsObj = requestBody.get("bookIds");
        if (bookIdsObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A lista de IDs de livros é obrigatória."));
        }
        if (!(bookIdsObj instanceof List)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A lista de IDs de livros deve ser uma lista."));
        }
        List<?> rawBookIds = (List<?>) bookIdsObj;
        if (rawBookIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Pelo menos um livro deve ser selecionado para o agendamento."));
        }
        try {
            bookIds = rawBookIds.stream()
                    .map(id -> {
                        if (id instanceof Integer) {
                            return ((Integer) id).longValue(); // Jackson pode desserializar números como Integer
                        } else if (id instanceof Long) {
                            return (Long) id;
                        }
                        throw new ClassCastException("ID do livro deve ser um número inteiro.");
                    })
                    .collect(Collectors.toList());
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "IDs de livros devem ser números inteiros válidos."));
        }


        // 3. Buscar e Validar Livros Selecionados
        List<Book> selectedBooks = new ArrayList<>();
        BigDecimal totalRentalPrice = BigDecimal.ZERO; // Inicializa o preço total
        for (Long bookId : bookIds) {
            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isEmpty() || bookOptional.get().getStatus() == Book.Status.OFF) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "Livro com ID " + bookId + " não encontrado ou inativo."));
            }
            Book book = bookOptional.get();
            if (book.getStock() == null || book.getStock() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "Livro '" + book.getTitle() + "' (ID: " + bookId + ") está fora de estoque."));
            }
            selectedBooks.add(book);

            // Calcula e adiciona o preço deste livro ao total
            // Multiplica o preço do livro pela duração em dias
            totalRentalPrice = totalRentalPrice.add(book.getPrice().multiply(BigDecimal.valueOf(durationDays)));
        }

        // 4. Criar o Agendamento (Schedule)
        Schedule schedule = Schedule.builder()
                .scheduleDate(scheduleDate)
                .durationDays(durationDays)
                .account(loggedUser) // Associa o agendamento ao usuário logado
                .build();
        // O status é default SCHEDULED e createdAt é @PrePersist

        // 5. Criar os Itens do Agendamento (ScheduleItem) e Atualizar Estoque dos Livros
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        for (Book book : selectedBooks) {
            ScheduleItem item = ScheduleItem.builder()
                    .schedule(schedule) // Associa o item ao agendamento recém-criado
                    .book(book)
                    .quantity(1) // Conforme especificado, sempre 1 unidade
                    .build();
            scheduleItems.add(item);

            // Decrementar o estoque do livro
            book.setStock(book.getStock() - 1);
            bookRepository.save(book); // Salva a atualização do estoque
        }

        // 6. Salvar o Agendamento e seus Itens
        schedule.setItems(scheduleItems); // Associa os itens ao agendamento antes de salvar o agendamento
        scheduleRepository.save(schedule); // Salva o agendamento (e os itens em cascata)

        // 7. Retornar Resposta de Sucesso
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("scheduleId", schedule.getId());
        responseData.put("scheduleDate", schedule.getScheduleDate());
        responseData.put("durationDays", schedule.getDurationDays());
        responseData.put("status", schedule.getStatus().name());
        responseData.put("accountId", loggedUser.getId());
        responseData.put("bookedBooks", selectedBooks.stream()
                .map(book -> Map.of("id", book.getId(), "title", book.getTitle(), "price", book.getPrice())) // Adicionado preço individual do livro
                .collect(Collectors.toList()));
        responseData.put("totalRentalPrice", totalRentalPrice); // Adiciona o preço total do aluguel

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Agendamento de aluguel criado com sucesso.", responseData));
    }
}
