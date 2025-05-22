package com.books.api.controller.schedule;

import com.books.api.dto.ScheduleCreateRequest;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> createSchedule(@Valid @RequestBody ScheduleCreateRequest request, HttpServletRequest httpRequest) {
        // 1. Autenticação e Autorização do Usuário
        Account loggedUser = jwtUtil.getLoggedUser(httpRequest, accountRepository);
        if (loggedUser == null || loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado ou inativo."));
        }

        // 2. Validação da Data do Agendamento (já feita pelo @Valid e @FutureOrPresent no DTO)
        // 3. Validação da Duração (já feita pelo @Valid e @Min no DTO)

        // 4. Buscar e Validar Livros Selecionados
        List<Book> selectedBooks = new ArrayList<>();
        for (Long bookId : request.getBookIds()) {
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
        }

        // 5. Criar o Agendamento (Schedule)
        Schedule schedule = Schedule.builder()
                .scheduleDate(request.getScheduleDate())
                .durationDays(request.getDurationDays())
                .account(loggedUser) // Associa o agendamento ao usuário logado
                .build();
        // O status é default SCHEDULED e createdAt é @PrePersist

        // 6. Criar os Itens do Agendamento (ScheduleItem) e Atualizar Estoque dos Livros
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

        // 7. Salvar o Agendamento e seus Itens
        schedule.setItems(scheduleItems); // Associa os itens ao agendamento antes de salvar o agendamento
        scheduleRepository.save(schedule); // Salva o agendamento (e os itens em cascata)

        // 8. Retornar Resposta de Sucesso
        // Podemos retornar alguns detalhes do agendamento criado se necessário
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("scheduleId", schedule.getId());
        responseData.put("scheduleDate", schedule.getScheduleDate());
        responseData.put("durationDays", schedule.getDurationDays());
        responseData.put("status", schedule.getStatus().name());
        responseData.put("accountName", loggedUser.getName());
        responseData.put("bookedBooks", selectedBooks.stream()
                .map(book -> Map.of("id", book.getId(), "title", book.getTitle()))
                .collect(Collectors.toList()));


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Agendamento de aluguel criado com sucesso.", responseData));
    }
}
