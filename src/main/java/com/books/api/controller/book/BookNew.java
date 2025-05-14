package com.books.api.controller.book;

import com.books.api.model.Book;
import com.books.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookNew {

    @Autowired
    private BookRepository bookRepository;

    // Endpoint para cadastro de livro (Novo livro)
    @PostMapping("/new")
    public ResponseEntity<Book> registerBook(@RequestBody Book book) {
        // Salvando o livro no banco
        Book savedBook = bookRepository.save(book);
        // Retornando resposta com o livro criado
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }
}