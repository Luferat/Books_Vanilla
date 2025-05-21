package com.books.api.controller.book;

import com.books.api.model.Book;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class SearchController {

    private final BookRepository bookRepository;

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam String query) {
        // Realiza a pesquisa no repositório, filtrando por status ON
        List<Book> foundBooks = bookRepository.searchByTermInMultipleFields(Book.Status.ON, query);

        // Verifica se algum livro foi encontrado
        if (foundBooks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Nenhum livro ativo encontrado para o termo de pesquisa: '" + query + "'."));
        }

        // Mapeia os objetos Book para Map<String, Object> com os campos desejados
        List<Map<String, Object>> bookList = foundBooks.stream()
                .map(book -> {
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
                    return bookInfo;
                })
                .collect(Collectors.toList());

        // Retorna a resposta de sucesso com a lista de livros
        return ResponseEntity.ok(
                ApiResponse.success("200", "Livros encontrados com sucesso para o termo: '" + query + "'.", bookList)
        );
    }
}
