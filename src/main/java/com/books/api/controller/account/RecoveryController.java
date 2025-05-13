package com.books.api.controller.account;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.AppUtil;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class RecoveryController {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final AppUtil util;

    @PostMapping("/recovery")
    public ResponseEntity<?> recovery(@RequestBody Map<String, Object> body, HttpServletRequest request) {

        // Bloqueia se o usuário estiver logado
        if (jwtUtil.getLoggedUser(request, accountRepository) != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("ALREADY_LOGGED", "Usuário já está autenticado."));
        }

        // Extrai dados do body
        String email = String.valueOf(body.get("email"));
        String cpf = String.valueOf(body.get("cpf"));
        String birth = String.valueOf(LocalDate.parse((CharSequence) body.get("birth")));

        // Busca a conta com os dados fornecidos
        Account account = accountRepository
                .findByEmailAndCpfAndBirth(email, cpf, LocalDate.parse(birth))
                .filter(acc -> acc.getStatus() == Account.Status.ON)
                .orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("NOT_FOUND", "Conta não localizada com os dados fornecidos."));
        }

        // Gera nova senha
        String newPassword = util.generatePassword(7, 10);

        // Criptografa e atualiza
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        account.setPassword(hashed);
        accountRepository.save(account);

        // Retorna a nova senha
        return ResponseEntity.ok(ApiResponse.success("200", "Nova senha gerada com sucesso.", Map.of("newPassword", newPassword)));
    }
}
