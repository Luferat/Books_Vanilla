package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.service.ConfigService;
import com.books.api.util.ApiResponse;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class NewAccountController {

    private final AccountRepository accountRepo;
    private final ConfigService config;
    private final JwtUtil jwt;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> body, HttpServletRequest request) {

        // Impede usuários logados de criarem novas contas
        Account loggedUser = jwt.getLoggedUser(request, accountRepo);
        if (loggedUser != null) {
            return ResponseEntity.status(403).body(ApiResponse.error("403", "Usuários logados não podem criar nova conta."));
        }

        // Validação de campos obrigatórios
        String[] required = {"name", "email", "password", "password2", "cpf", "tel", "birth", "address"};
        for (String key : required) {
            if (!body.containsKey(key) || body.get(key).isBlank()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("400", "O campo '" + key + "' é obrigatório."));
            }
        }

        // Validação de senha
        if (!body.get("password").equals(body.get("password2"))) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "As senhas não coincidem."));
        }

        // Email em minúsculas
        String email = body.get("email").toLowerCase();

        // Verifica duplicidade de email ou CPF
        Optional<Account> checkEmail = accountRepo.findByEmail(email);
        Optional<Account> checkCpf = accountRepo.findByCpf(body.get("cpf"));

        if (checkEmail.isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "Este e-mail já está em uso."));
        }

        if (checkCpf.isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "Este CPF já está em uso."));
        }

        // Cria nova conta
        Account acc = new Account();
        acc.setId(null);
        acc.setName(body.get("name"));
        acc.setEmail(email);
        acc.setPassword(BCrypt.hashpw(body.get("password"), BCrypt.gensalt()));
        acc.setCpf(body.get("cpf"));
        acc.setTel(body.get("tel"));
        acc.setBirth(LocalDate.parse(body.get("birth")));
        acc.setAddress(body.get("address"));
        acc.setRole(Account.Role.USER);
        acc.setStatus(Account.Status.ON);
        acc.setPhoto(config.get("defaultUserPhoto")); // valor vindo do banco
        acc.setMetadata("{}");

        acc = accountRepo.save(acc);

        Map<String, Object> data = Map.of(
                "id", acc.getId(),
                "name", acc.getName(),
                "email", acc.getEmail(),
                "cpf", acc.getCpf(),
                "tel", acc.getTel(),
                "birth", acc.getBirth(),
                "address", acc.getAddress(),
                "photo", acc.getPhoto(),
                "role", acc.getRole()
        );

        return ResponseEntity.ok(ApiResponse.success("201", "Conta criada com sucesso.", data));
    }
}
