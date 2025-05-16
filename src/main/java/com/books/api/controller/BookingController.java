// Pacote onde está localizado o controller
package com.books.api.controller;

// Importações necessárias
import com.books.api.model.Booking; // Classe que representa o agendamento
import com.books.api.model.BookingStatus; // Enum com os status possíveis do agendamento
import com.books.api.repository.BookingRepository; // Interface de acesso ao banco de dados
import com.books.api.util.ApiResponse; // Classe utilitária para retornar um JSON padronizado
import lombok.RequiredArgsConstructor; // Anotação Lombok que cria um construtor com os campos finais
import org.springframework.http.ResponseEntity; // Representa uma resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações para criar a API REST

import java.time.LocalDateTime; // Classe para trabalhar com data e hora
import java.util.List;
import java.util.Map;

@RestController // Indica que essa classe é um controller REST que responde requisições HTTP
@RequestMapping("/bookings") // Define o caminho base da rota (ex: /bookings)
@RequiredArgsConstructor // Lombok: cria um construtor para injetar dependências final
public class BookingController {

    private final BookingRepository bookingRepository; // Injeta o repositório para salvar/consultar no banco

    // 1. AGENDAR NOVO AGENDAMENTO
    @PostMapping
    public ResponseEntity<Map<String, Object>> schedule(@RequestBody Booking booking) {
        booking.setBookingDate(LocalDateTime.now()); // Define a data atual como data do agendamento
        booking.setStatus(BookingStatus.SCHEDULED); // Define o status inicial como "AGENDADO"
        Booking saved = bookingRepository.save(booking); // Salva o agendamento no banco
        return ResponseEntity.ok(
                ApiResponse.success("201", "Agendamento realizado com sucesso!", saved)
        ); // Retorna resposta padrão com status 201
    }

    // 2. CONSULTAR TODOS OS AGENDAMENTOS
    @GetMapping
    public ResponseEntity<Map<String, Object>> listAll() {
        List<Booking> bookings = bookingRepository.findAll(); // Busca todos os agendamentos do banco
        return ResponseEntity.ok(
                ApiResponse.success("200", "Lista de agendamentos", bookings)
        ); // Retorna a lista com código 200 e mensagem
    }

    // 3. CONSULTAR UM AGENDAMENTO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        // Busca por ID e retorna sucesso ou erro 404 se não encontrar
        return bookingRepository.findById(id)
                .map(b -> ResponseEntity.ok(
                        ApiResponse.success("200", "Agendamento encontrado", b)
                ))
                .orElse(ResponseEntity.status(404).body(
                        ApiResponse.error("404", "Agendamento não encontrado")
                ));
    }

    // 4. ATUALIZAR DADOS DO AGENDAMENTO
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Booking newData) {
        return bookingRepository.findById(id).map(b -> {
            b.setCustomerName(newData.getCustomerName()); // Atualiza o nome do cliente
            b.setBookTitle(newData.getBookTitle());       // Atualiza o título do livro
            b.setStatus(BookingStatus.UPDATED);           // Define o status como atualizado
            Booking updated = bookingRepository.save(b);  // Salva as alterações
            return ResponseEntity.ok(
                    ApiResponse.success("200", "Agendamento atualizado", updated)
            );
        }).orElse(ResponseEntity.status(404).body(
                ApiResponse.error("404", "Agendamento não encontrado")
        ));
    }

    // 5. MARCAR COMO DEVOLVIDO
    @PatchMapping("/{id}/return")
    public ResponseEntity<Map<String, Object>> markAsReturned(@PathVariable Long id) {
        return bookingRepository.findById(id).map(b -> {
            b.setReturnDate(LocalDateTime.now());        // Define a data de devolução como agora
            b.setStatus(BookingStatus.RETURNED);         // Muda o status para DEVOLVIDO
            Booking updated = bookingRepository.save(b); // Salva no banco
            return ResponseEntity.ok(
                    ApiResponse.success("200", "Livro marcado como devolvido", updated)
            );
        }).orElse(ResponseEntity.status(404).body(
                ApiResponse.error("404", "Agendamento não encontrado")
        ));
    }

    // 6. CANCELAR UM AGENDAMENTO
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable Long id) {
        return bookingRepository.findById(id).map(b -> {
            b.setStatus(BookingStatus.CANCELED);         // Muda o status para CANCELADO
            bookingRepository.save(b);                   // Salva no banco
            return ResponseEntity.ok(
                    ApiResponse.success("200", "Agendamento cancelado", b)
            );
        }).orElse(ResponseEntity.status(404).body(
                ApiResponse.error("404", "Agendamento não encontrado")
        ));
    }
}
