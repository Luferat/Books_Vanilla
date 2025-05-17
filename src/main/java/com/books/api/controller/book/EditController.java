package com.books.api.controller.book;

import com.books.api.model.Account;
import com.books.api.model.Book;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/book")
public class EditController {

    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public EditController(BookRepository bookRepository, AccountRepository accountRepository, JwtUtil jwtUtil) {
        this.bookRepository = bookRepository;
        this.accountRepository = accountRepository;
        this.jwtUtil = jwtUtil;
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editBook(
            @PathVariable Long id,
            @RequestBody Book updatedBook,
            HttpServletRequest request
    ) {
        // 1. Verifica autenticação
        Account loggedUser = jwtUtil.getLoggedUser(request, accountRepository);
        if (loggedUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("401", "Não autenticado ou token inválido."));
        }

        // 2. Verifica autorização
        if (loggedUser.getRole() != Account.Role.ADMIN && loggedUser.getRole() != Account.Role.OPERATOR) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("403", "Acesso negado. Requer role de ADMIN ou OPERATOR."));
        }

        // 3. Edita livro se encontrado
        return bookRepository.findById(id)
                .map(book -> {
                    if (updatedBook.getTitle() != null) book.setTitle(updatedBook.getTitle());
                    if (updatedBook.getAuthor() != null) book.setAuthor(updatedBook.getAuthor());
                    if (updatedBook.getPublicationYear() != null) book.setPublicationYear(updatedBook.getPublicationYear());
                    if (updatedBook.getCoverImageUrl() != null) book.setCoverImageUrl(updatedBook.getCoverImageUrl());
                    if (updatedBook.getGenre() != null) book.setGenre(updatedBook.getGenre());
                    if (updatedBook.getSynopsis() != null) book.setSynopsis(updatedBook.getSynopsis());
                    if (updatedBook.getStatus() != null) book.setStatus(updatedBook.getStatus());


                    Book savedBook = bookRepository.save(book);
                    return ResponseEntity.ok(
                            ApiResponse.success("200", "Livro editado com sucesso", savedBook)
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.status(404)
                                .body(ApiResponse.error("404", "Livro não encontrado"))
                );
    }
}