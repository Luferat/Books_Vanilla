package com.books.api.controller.book;

import com.books.api.model.Book;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class ListController {

    private final BookRepository bookRepository;

    @GetMapping("/list")
    public ResponseEntity<?> listActiveBooks() {
        List<Book> activeBooks = bookRepository.findByStatus(Book.Status.ON);

        if (activeBooks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Nenhum livro ativo encontrado."));
        }

        List<Map<String, Object>> bookList = activeBooks.stream()
                .map(this::convertToBookInfo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("200", "Lista de livros ativos carregada com sucesso.", bookList)
        );
    }

    @GetMapping("/list/{field}/{dir}")
    public ResponseEntity<?> listActiveBooksOrdered(@PathVariable String field, @PathVariable String dir) {
        Sort.Direction direction;
        if ("desc".equalsIgnoreCase(dir)) {
            direction = Sort.Direction.DESC;
        } else if ("asc".equalsIgnoreCase(dir)) {
            direction = Sort.Direction.ASC;
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("400", "Direção de ordenação inválida. Use 'asc' ou 'desc'."));
        }

        Sort sort;
        switch (field.toLowerCase()) {
            case "title":
                sort = Sort.by(direction, "title");
                break;
            case "author":
                sort = Sort.by(direction, "author");
                break;
            case "genre":
                sort = Sort.by(direction, "genre");
                break;
            case "ebook":
                sort = Sort.by(direction, "ebook");
                break;
            case "language":
                sort = Sort.by(direction, "language");
                break;
            case "price":
                sort = Sort.by(direction, "price");
                break;
            default:
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("400", "Campo de ordenação inválido. Use 'title', 'author', 'genre', 'ebook', 'language' ou 'price'."));
        }

        List<Book> activeBooks = bookRepository.findByStatus(Book.Status.ON, sort);

        if (activeBooks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Nenhum livro ativo encontrado."));
        }

        List<Map<String, Object>> bookList = activeBooks.stream()
                .map(this::convertToBookInfo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("200", "Lista de livros ativos ordenada por '" + field + "' (" + dir.toUpperCase() + ") carregada com sucesso.", bookList)
        );
    }

    private Map<String, Object> convertToBookInfo(Book book) {
        Map<String, Object> bookInfo = new HashMap<>();
        bookInfo.put("id", book.getId());
        bookInfo.put("title", book.getTitle());
        bookInfo.put("language", book.getLanguage());
        bookInfo.put("publicationYear", book.getPublicationYear());
        bookInfo.put("genre", book.getGenre());
        bookInfo.put("synopsis", book.getSynopsis());
        bookInfo.put("coverImageUrl", book.getCoverImageUrl());
        bookInfo.put("price", book.getPrice());
        bookInfo.put("ebook", book.isEbook());
        bookInfo.put("stock", book.getStock());
        return bookInfo;
    }
}