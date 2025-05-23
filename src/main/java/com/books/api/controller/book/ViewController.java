package com.books.api.controller.book;

import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class ViewController {

    private final BookRepository bookRepository;

    @GetMapping("/view/{id}")
    public ResponseEntity<Map<String, Object>> viewBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("id", book.getId());
                    data.put("title", book.getTitle());
                    data.put("author", book.getAuthor());
                    data.put("isbn", book.getIsbn());
                    data.put("publisher", book.getPublisher());
                    data.put("publicationYear", book.getPublicationYear());
                    data.put("coverImageUrl", book.getCoverImageUrl());
                    data.put("numberOfPages", book.getNumberOfPages());
                    data.put("editionNumber", book.getEditionNumber());
                    data.put("synopsis", book.getSynopsis()); // Aqui mostramos completa
                    data.put("genre", book.getGenre());
                    data.put("hasDrm", book.getHasDrm());
                    data.put("format", book.getFormat());
                    data.put("language", book.getLanguage());
                    data.put("ebook", book.isEbook());
                    data.put("price", book.getPrice());
                    data.put("stock", book.getStock());

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
