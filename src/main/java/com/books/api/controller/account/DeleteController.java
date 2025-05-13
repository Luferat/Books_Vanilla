package com.books.api.controller.account;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.CookieUtil;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class DeleteController {

    private final AccountRepository accountRepo;
    private final JwtUtil jwt;
    private final CookieUtil cookieUtil;

    @PatchMapping("/delete")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request, HttpServletResponse response) {

        // Obtém o usuário logado
        Account loggedUser = jwt.getLoggedUser(request, accountRepo);

        // Verifica se há um usuário logado
        if (loggedUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("401", "Não autenticado."));
        }

        // Verifica se a conta do usuário está ativa
        if (loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(400).body(ApiResponse.error("400", "Sua conta já está inativa ou não pode ser apagada."));
        }

        try {
            // Altera o status da conta para OFF
            loggedUser.setStatus(Account.Status.OFF);
            accountRepo.save(loggedUser);

            // Apaga todos os cookies
            cookieUtil.removeCookie("token", response);
            cookieUtil.removeCookie("userdata", response);

            return ResponseEntity.ok(ApiResponse.success("200", "Conta apagada com sucesso."));

        } catch (Exception e) {
            // Log do erro para depuração (opcional)
            return ResponseEntity.status(500).body(ApiResponse.error("500", "Erro ao apagar a conta."));
        }
    }
}