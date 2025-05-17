package com.books.api.controller.barreiras;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VerifyUser {


    private final AccountRepository accountRepository;
    private final JwtUtil jwt;

    public ResponseEntity<?> verifyUser(HttpServletRequest request){

        String token = jwt.extractTokenFromCookies(request);
        Long userId = jwt.getUserId(token);
        Account loggedUser = jwt.getLoggedUser(request, accountRepository);

        // Impede usuários não logados de adicionarem novos livros
        if (loggedUser == null) {
            return ResponseEntity.status(403).body(ApiResponse.error("403", "Logue para adicionar livros."));
        }

        Account account = accountRepository.findById(userId).orElse(null);

        //Impede usuários comuns de adicionarem novos livros
        if(account.getRole() != Account.Role.ADMIN && account.getRole() != Account.Role.OPERATOR){
            return ResponseEntity.status(403).body(ApiResponse.error("403", "Apenas administradores e operadores podem adicionar livros"));
        }

        return ResponseEntity.status(200).body(ApiResponse.error("200", "sucesso"));
    }
}
