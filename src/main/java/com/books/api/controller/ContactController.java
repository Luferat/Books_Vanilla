package com.books.api.controller;

import com.books.api.model.Contact;
import com.books.api.repository.ContactRepository;
import com.books.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createContact(@RequestBody Contact contact) {

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
        contact.setStatus(Contact.Status.RECEIVED);

        // Salva no banco
        Contact saved = contactRepository.save(contact);

        // Resposta de sucesso
        return ResponseEntity.ok(ApiResponse.success("201", "Contato salvo com sucesso!", saved));
    }

    // Método auxiliar para validar strings
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
