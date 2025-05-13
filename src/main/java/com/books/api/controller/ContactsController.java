package com.books.api.controller;

import com.books.api.model.Contacts;
import com.books.api.repository.ContactsRepository;
import com.books.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactsController {

    private final ContactsRepository contactsRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createContact(@RequestBody Contacts contact) {

        // Validação manual dos campos obrigatórios
        if (isNullOrEmpty(contact.getName())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "O campo 'Nome' é obrigatório."));
        }
        if (isNullOrEmpty(contact.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "O campo 'Email' é obrigatório."));
        }
        if (isNullOrEmpty(contact.getSubject())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "O campo 'Assunto' é obrigatório."));
        }
        if (isNullOrEmpty(contact.getMessage())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "O campo 'Mensagem' é obrigatório."));
        }

        // Define valores padrão
        contact.setDate(LocalDateTime.now());
        contact.setStatus(Contacts.Status.RECEIVED);

        // Salva no banco
        Contacts saved = contactsRepository.save(contact);

        // Resposta de sucesso
        return ResponseEntity.ok(ApiResponse.success("201", "Contato salvo com sucesso!", saved));
    }

    // Método auxiliar para validar strings
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
