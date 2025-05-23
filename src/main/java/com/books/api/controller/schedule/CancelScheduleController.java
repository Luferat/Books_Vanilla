package com.books.api.controller.schedule;

import com.books.api.model.Account;
import com.books.api.model.Book;
import com.books.api.model.Schedule;
import com.books.api.model.ScheduleItem;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository; // Importar BookRepository
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
    private final BookRepository bookRepository; // Injetar BookRepository

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
        // É importante que o agendamento seja carregado com seus itens para gerenciar o estoque.
        // Se findById não carregar eagermente, considere um método customizado no repositório.
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

        // 6. Reverter o estoque dos livros associados ao agendamento
        // É crucial que os ScheduleItems e os Books estejam carregados (eagerly ou lazy dentro da transação)
        // para que esta operação funcione corretamente.
        if (schedule.getItems() != null) {
            for (ScheduleItem item : schedule.getItems()) {
                Book book = item.getBook();
                if (book != null) {
                    book.setStock(book.getStock() + item.getQuantity()); // Incrementa o estoque
                    bookRepository.save(book); // Salva a atualização do livro
                }
            }
        }

        // 7. Atualizar o Status do Agendamento para CANCELED
        schedule.setStatus(Schedule.Status.CANCELED);
        scheduleRepository.save(schedule);

        // 8. Retornar Resposta de Sucesso
        return ResponseEntity.ok(ApiResponse.success("200", "Agendamento cancelado com sucesso e estoque revertido."));
    }
}
