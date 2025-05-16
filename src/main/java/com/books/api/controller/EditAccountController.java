package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class    EditAccountController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @PutMapping("/edit")
    public ResponseEntity<?> editAccount(@RequestBody Map<String, Object> body, HttpServletRequest request) {

        // Pega o token do cookie
        String token = jwtUtil.extractTokenFromCookies(request);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Token inválido ou ausente."));
        }

        // Busca o usuário logado
        Long userId = jwtUtil.getUserId(token);
        Account account = accountRepository.findById(userId).orElse(null);

        if (account == null || account.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Conta inválida ou desativada."));
        }

        // Atualiza os campos permitidos
        if (body.containsKey("name")) {
            account.setName(String.valueOf(body.get("name")));
        }
        if (body.containsKey("tel")) {
            account.setTel(String.valueOf(body.get("tel")));
        }
        if (body.containsKey("address")) {
            account.setAddress(String.valueOf(body.get("address")));
        }

        // Salva alterações
        accountRepository.save(account);

        return ResponseEntity.ok(ApiResponse.success("200", "Dados atualizados com sucesso."));
    }
}