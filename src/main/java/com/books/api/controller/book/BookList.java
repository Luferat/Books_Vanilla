package com.books.api.controller.book;

import com.books.api.model.Book;
import com.books.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookList {

    @Autowired
    private BookRepository bookRepository;

    // Endpoint para listar todos os livros cadastrados
    @GetMapping("/list")
    public ResponseEntity<Iterable<Book>> listBooks() {
        // Retornando todos os livros
        Iterable<Book> books = bookRepository.findAllByOrderByTitleAsc();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Endpoint para visualizar um livro específico pelo ID
    @GetMapping("/view/{id}")
    public ResponseEntity<Book> viewBook(@PathVariable Long id) {
        // Procurando o livro pelo ID
        return bookRepository.findById(id)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}