package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.CookieUtil;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class PasswordController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final CookieUtil cookieUtil;

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {

        // Pega token do cookie
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

        // Extrai dados do body
        String email = String.valueOf(body.get("email"));
        String actual = String.valueOf(body.get("actual"));
        String new1 = String.valueOf(body.get("new1"));
        String new2 = String.valueOf(body.get("new2"));

        // Valida e-mail e senha atual
        if (!account.getEmail().equalsIgnoreCase(email) || !BCrypt.checkpw(actual, account.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("400", "E-mail ou senha atual incorretos."));
        }

        // Valida nova senha
        if (!new1.equals(new2)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("400", "As novas senhas não coincidem."));
        }

        if (new1.length() < 7) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("400", "A nova senha deve ter pelo menos 7 caracteres."));
        }

        // Atualiza senha
        account.setPassword(BCrypt.hashpw(new1, BCrypt.gensalt()));
        accountRepository.save(account);

        cookieUtil.removeCookie("token", response);
        cookieUtil.removeCookie("userdata", response);

        return ResponseEntity.ok(ApiResponse.success("200", "Senha alterada com sucesso."));
    }
}
