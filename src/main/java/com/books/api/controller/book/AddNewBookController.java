package com.books.api.controller.book;

import com.books.api.config.Config;
import com.books.api.model.Book;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class AddNewBookController {

    @Autowired
    private final BookRepository bookRepo;

    private final JwtUtil jwt;

    @PostMapping("/new")
    public ResponseEntity<?> newBook(@RequestBody Map<String, String> body, HttpServletRequest request){

        String[] require = {"title", "author", "publicationyear"};
        for(String key : require){
            if(body.get(key).isBlank()){
                return ResponseEntity.badRequest().body(ApiResponse.error("400", "O campo '" + key + "' é obrigatório."));
            }
        }

        Book book = new Book();




        book.setAuthor(body.get("author"));
        System.out.println("Autor: " + book.getAuthor());

        book.setTitle(body.get("title"));
        System.out.println("Título: " + book.getTitle());

        book.setPublicationYear(Integer.parseInt(body.get("publicationyear")));
        System.out.println("Ano de publicação: " + book.getPublicationYear());

        book.setIsbn(body.get("isbn"));
        System.out.println("Isbn: " + book.getIsbn());

        book.setGenre(body.get("genre"));
        System.out.println("Genero do livro: " + book.getGenre());

        return ResponseEntity.ok(ApiResponse.success("201", "Livro registrado com sucesso."));
    }

}
