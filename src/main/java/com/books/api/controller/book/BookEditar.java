package com.books.api.controller.book;

import com.books.api.controller.barreiras.VerifyUser;
import com.books.api.model.Account;
import com.books.api.model.Book;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.GetNullBodyBook;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookEditar {

    /*
    * ESSE CÓDIGO È DO HUGO
    *   fiz algumas modificações como a implementação do VerifyUser
    *   ass: Burnier
    * */

    private final BookRepository bookRepository;
    private final VerifyUser verifyUser;

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editBook(
            @PathVariable Long id,
            @RequestBody Book updatedBook,
            HttpServletRequest request
    ) {

        ResponseEntity<?> response = verifyUser.verifyUser(request);

        //usa o barreiras/verifyuser para ver se o usuario e valido para fazer a ação.
        if (response.getStatusCodeValue() != 200) {
            return response;
        }


        // 3. Edita livro se encontrado
        return bookRepository.findById(id)
                .map(book -> {


                    BeanUtils.copyProperties(updatedBook, book, GetNullBodyBook.getNullPropertyNames(updatedBook));

                    Book savedBook = bookRepository.save(book);

                    return ResponseEntity.ok(
                            ApiResponse.success("200", "Livro atualizado com sucesso", savedBook)
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.status(404)
                                .body(ApiResponse.error("404", "Livro não encontrado"))
                );
    }
}
