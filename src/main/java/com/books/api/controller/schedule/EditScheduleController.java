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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Usando POST para consistência com o padrão existente (delete)
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
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class EditScheduleController {

    // Dados do usuário de Teste
    int loggedUserId = 1;
    String loggedUserName = "Joca da Silva";

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;
    private final ScheduleRepository scheduleRepository;

    @PostMapping("/edit/{id}")
    @Transactional // Garante que a operação seja atômica
    public ResponseEntity<?> editSchedule(@PathVariable Long id, @RequestBody Map<String, Object> requestBody, HttpServletRequest httpRequest) {

        /** Desabilitado para experimentos
        // 1. Autenticação e Autorização do Usuário
        Account loggedUser = jwtUtil.getLoggedUser(httpRequest, accountRepository);
        if (loggedUser == null || loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado ou inativo."));
        }
        **/

        // 2. Buscar o Agendamento pelo ID
        // É importante que o agendamento seja carregado com seus itens para gerenciar o estoque.
        // Se findById não carregar eagermente, considere um método customizado no repositório.
        // Assumindo que findById dentro de @Transactional carregará os itens lazy.
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);
        if (scheduleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Agendamento não encontrado."));
        }

        Schedule schedule = scheduleOptional.get();

        // 3. Verificar se o agendamento pertence ao usuário logado
        // if (!schedule.getAccount().getId().equals(loggedUser.getId())) { // Desabilitado para experimentos
        if (!schedule.getAccount().getId().equals(loggedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("403", "Acesso negado. Este agendamento não pertence ao usuário logado."));
        }

        // 4. Verificar se o agendamento já está cancelado ou retornado (não pode ser editado)
        if (schedule.getStatus() == Schedule.Status.CANCELED || schedule.getStatus() == Schedule.Status.RETURNED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Não é possível editar um agendamento cancelado ou devolvido."));
        }

        // 5. Extrair e Validar Dados da Requisição (Manual)
        LocalDateTime newScheduleDate;
        Integer newDurationDays;
        List<Long> newBookIds;

        // newScheduleDate
        Object scheduleDateObj = requestBody.get("scheduleDate");
        if (scheduleDateObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A nova data do agendamento é obrigatória."));
        }
        try {
            newScheduleDate = LocalDateTime.parse(scheduleDateObj.toString());
            // A data do agendamento original DEVE estar no futuro para edição
            if (schedule.getScheduleDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "Não é possível editar um agendamento com data passada."));
            }
            // A nova data do agendamento também não pode ser no passado
            if (newScheduleDate.isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "A nova data do agendamento não pode ser no passado."));
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Formato de nova data do agendamento inválido. Use YYYY-MM-DD'T'HH:mm:ss."));
        }

        // newDurationDays
        Object durationDaysObj = requestBody.get("durationDays");
        if (durationDaysObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A nova duração em dias é obrigatória."));
        }
        try {
            if (durationDaysObj instanceof Integer) {
                newDurationDays = (Integer) durationDaysObj;
            } else if (durationDaysObj instanceof Long) {
                newDurationDays = ((Long) durationDaysObj).intValue();
            } else {
                throw new ClassCastException();
            }

            if (newDurationDays < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "A nova duração deve ser de pelo menos 1 dia."));
            }
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Nova duração em dias deve ser um número inteiro."));
        }

        // newBookIds
        Object bookIdsObj = requestBody.get("bookIds");
        if (bookIdsObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A nova lista de IDs de livros é obrigatória."));
        }
        if (!(bookIdsObj instanceof List)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "A nova lista de IDs de livros deve ser uma lista."));
        }
        List<?> rawNewBookIds = (List<?>) bookIdsObj;
        if (rawNewBookIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Pelo menos um livro deve ser selecionado para o agendamento."));
        }
        try {
            newBookIds = rawNewBookIds.stream()
                    .map(idVal -> {
                        if (idVal instanceof Integer) {
                            return ((Integer) idVal).longValue();
                        } else if (idVal instanceof Long) {
                            return (Long) idVal;
                        }
                        throw new ClassCastException("ID do livro deve ser um número inteiro.");
                    })
                    .collect(Collectors.toList());
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Novos IDs de livros devem ser números inteiros válidos."));
        }

        // 6. Atualizar os campos do Agendamento
        schedule.setScheduleDate(newScheduleDate);
        schedule.setDurationDays(newDurationDays);
        schedule.setStatus(Schedule.Status.UPDATED); // Opcional: Mudar status para UPDATED

        // 7. Gerenciar a lista de livros (ScheduleItems) e o estoque
        Set<Long> currentBookIds = schedule.getItems().stream()
                .map(item -> item.getBook().getId())
                .collect(Collectors.toSet());

        // Livros a serem removidos (estoque incrementado)
        List<ScheduleItem> itemsToRemove = new ArrayList<>();
        for (ScheduleItem item : schedule.getItems()) {
            if (!newBookIds.contains(item.getBook().getId())) {
                item.getBook().setStock(item.getBook().getStock() + 1); // Reverter estoque
                bookRepository.save(item.getBook()); // Salvar atualização do livro
                itemsToRemove.add(item);
            }
        }
        schedule.getItems().removeAll(itemsToRemove); // Remove os itens da coleção

        // Livros a serem adicionados (estoque decrementado)
        List<Book> booksToAdd = new ArrayList<>();
        for (Long bookId : newBookIds) {
            if (!currentBookIds.contains(bookId)) {
                Optional<Book> bookOptional = bookRepository.findById(bookId);
                if (bookOptional.isEmpty() || bookOptional.get().getStatus() == Book.Status.OFF) {
                    // Se um livro para adicionar não for encontrado ou estiver inativo, reverter transação
                    throw new RuntimeException("Livro com ID " + bookId + " não encontrado ou inativo para adicionar.");
                }
                Book book = bookOptional.get();
                if (book.getStock() == null || book.getStock() <= 0) {
                    throw new RuntimeException("Livro '" + book.getTitle() + "' (ID: " + bookId + ") está fora de estoque para adicionar.");
                }
                book.setStock(book.getStock() - 1); // Decrementar estoque
                bookRepository.save(book); // Salvar atualização do livro
                booksToAdd.add(book);
            }
        }

        // Criar novos ScheduleItems para os livros a serem adicionados
        for (Book book : booksToAdd) {
            ScheduleItem newItem = ScheduleItem.builder()
                    .schedule(schedule)
                    .book(book)
                    .quantity(1)
                    .build();
            schedule.getItems().add(newItem); // Adiciona o novo item à coleção
        }

        // 8. Salvar o Agendamento Atualizado
        scheduleRepository.save(schedule); // Salva o agendamento (itens serão gerenciados em cascata)

        // 9. Recalcular e Retornar Resposta de Sucesso
        BigDecimal totalRentalPrice = BigDecimal.ZERO;
        List<Map<String, Object>> updatedBookDetails = new ArrayList<>();

        // Itera sobre os itens ATUALIZADOS do agendamento para calcular o preço e detalhes
        for (ScheduleItem item : schedule.getItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("bookId", item.getBook().getId());
            itemMap.put("bookTitle", item.getBook().getTitle());
            itemMap.put("bookPrice", item.getBook().getPrice());
            BigDecimal itemRentalPrice = item.getBook().getPrice().multiply(BigDecimal.valueOf(schedule.getDurationDays()));
            itemMap.put("itemRentalPrice", itemRentalPrice);
            updatedBookDetails.add(itemMap);
            totalRentalPrice = totalRentalPrice.add(itemRentalPrice);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("scheduleId", schedule.getId());
        responseData.put("createdAt", schedule.getCreatedAt());
        responseData.put("scheduleDate", schedule.getScheduleDate());
        responseData.put("durationDays", schedule.getDurationDays());
        responseData.put("status", schedule.getStatus().name());
        // responseData.put("accountName", loggedUser.getName()); // Desabilitado para experimentos
        responseData.put("accountName", loggedUserName);
        responseData.put("bookedBooks", updatedBookDetails);
        responseData.put("totalRentalPrice", totalRentalPrice);

        return ResponseEntity.ok(
                ApiResponse.success("200", "Agendamento atualizado com sucesso.", responseData)
        );
    }
}
