package com.books.api.controller.book;

import com.books.api.model.Book;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/book")
public class BookList {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listBooks() {
        List<Book> books = bookRepository.findAllByOrderByTitleAsc();

        List<Map<String, Object>> filteredBooks = books.stream().map(book -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("title", book.getTitle());
            map.put("author", book.getAuthor());
            map.put("publicationYear", book.getPublicationYear());
            map.put("photo", book.getPhoto());

            // Limita a sinopse a no máximo 20 caracteres
            String synopsis = book.getSynopsis();
            String shortenedSynopsis = synopsis != null && synopsis.length() > 20
                    ? synopsis.substring(0, 20) + "..."
                    : synopsis;

            map.put("synopsis", shortenedSynopsis);
            return map;
        }).toList();

        return new ResponseEntity<>(
                ApiResponse.success("200", "Livros listados com sucesso", filteredBooks),
                HttpStatus.OK
        );
    }


    @GetMapping("/view/{id}")
    public ResponseEntity<Map<String, Object>> viewBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("title", book.getTitle());
                    data.put("author", book.getAuthor());
                    data.put("publicationYear", book.getPublicationYear());
                    data.put("photo", book.getPhoto());
                    data.put("synopsis", book.getSynopsis()); // Aqui mostramos completa
                    data.put("Genre", book.getGenre());


                    return new ResponseEntity<>(
                            ApiResponse.success("200", "Livro encontrado com sucesso", data),
                            HttpStatus.OK
                    );
                })
                .orElseGet(() -> new ResponseEntity<>(
                        ApiResponse.error("404", "Livro não encontrado"),
                        HttpStatus.NOT_FOUND
                ));
    }
}