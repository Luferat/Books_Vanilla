package com.books.api.controller.book;

import com.books.api.config.Config;
import com.books.api.controller.barreiras.VerifyUser;
import com.books.api.model.Account;
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

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class AddNewBookController {

    @Autowired
    private final BookRepository bookRepository;
    private final VerifyUser verifyUser;


    @PostMapping("/new")
    public ResponseEntity<?> newBook(@RequestBody Map<String, String> body, HttpServletRequest request){

            ResponseEntity<?> response = verifyUser.verifyUser(request);

            //usa o barreiras/verifyuser para ver se o usuario e valido para fazer a ação.
            if (response.getStatusCodeValue() != 200) {
                return response;
            }

            // Verifica se os campos estão preenchidos
            String[] require = {"title", "author", "publicationyear"};
            for(String key : require){
                if(body.get(key).isBlank()){
                    return ResponseEntity.badRequest().body(ApiResponse.error("400", "O campo '" + key + "' é obrigatório."));
                }
            }

            Book book = new Book();

            book.setId(null);
            book.setLaunch(LocalDate.now());
            book.setQuantity(Integer.parseInt(body.get("quantity")));
            book.setStatus(Book.Status.DISPONIVEL);
            book.setAuthor(body.get("author"));
            book.setTitle(body.get("title"));
            book.setPublicationYear(Integer.parseInt(body.get("publicationyear")));
            book.setIsbn(body.get("isbn"));
            book.setGenre(body.get("genre"));
            book.setPhoto(body.get("photo"));
            book.setSynopsis(body.get("synopsis"));
            book = bookRepository.save(book);
            return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

}
