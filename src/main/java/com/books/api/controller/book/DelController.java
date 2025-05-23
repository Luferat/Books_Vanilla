package com.books.api.controller.book;

import com.books.api.model.Account;
import com.books.api.model.Book;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@CrossOrigin
public class DelController {

    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id, HttpServletRequest request) {
        // 1. Verificar o Token e Obter o Usuário Logado
        Account loggedUser = jwtUtil.getLoggedUser(request, accountRepository);
        if (loggedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Não autenticado ou token inválido."));
        }

        // 2. Verificar a Role do Usuário
        if (loggedUser.getRole() != Account.Role.ADMIN && loggedUser.getRole() != Account.Role.OPERATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("403", "Acesso negado. Requer role de ADMIN ou OPERATOR."));
        }

        // 3. Buscar o Livro pelo ID
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Livro não encontrado."));
        }

        Book book = bookOptional.get();

        // 4. Verificar se o Livro já está apagado
        if (book.getStatus() == Book.Status.OFF) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("400", "Este livro já está apagado."));
        }

        // 5. Atualizar o Status do Livro para OFF
        book.setStatus(Book.Status.OFF);
        bookRepository.save(book);

        // 6. Retornar Resposta de Sucesso
        return ResponseEntity.ok(ApiResponse.success("200", "Livro apagado com sucesso."));
    }
}
