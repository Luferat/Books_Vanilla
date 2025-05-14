package com.books.api.controller.book;

import com.books.api.model.Book;
import com.books.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookUpdate {

    @Autowired
    private BookRepository bookRepository;

    // Endpoint para editar um livro existente pelo ID
    @PutMapping("/edit/{id}")
    public ResponseEntity<Book> editBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        // Verificando se o livro existe
        return bookRepository.findById(id)
                .map(book -> {
                    // Atualizando os dados do livro
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setPublicationYear(updatedBook.getPublicationYear());
                    book.setPhoto(updatedBook.getPhoto());
                    book.setGenre(updatedBook.getGenre());
                    book.setSynopsis(updatedBook.getSynopsis());
                    book.setStatus(updatedBook.getStatus());

                    // Salvando o livro atualizado
                    Book savedBook = bookRepository.save(book);
                    return new ResponseEntity<>(savedBook, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}