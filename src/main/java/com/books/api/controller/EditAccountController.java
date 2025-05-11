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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class EditAccountController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    private Account getAuthenticatedAccount(HttpServletRequest request) {
        // Verifica o token do usuário
        String token = jwtUtil.extractTokenFromCookies(request);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return null; // Retorna null se o token não for válido
        }

        Long userId = jwtUtil.getUserId(token); // Obtém o ID do usuário do token
        // Busca no repositório o Account (com "A" maiúsculo) correspondente ao ID do usuário
        return accountRepository.findById(userId)
                .filter(acc -> acc.getStatus() == Account.Status.ON) // Verifica se a conta está ativa
                .orElse(null); // Retorna a Account (ou null caso não seja encontrada)
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editAccount(@RequestBody Map<String, Object> body, HttpServletRequest request) {

        Account account = getAuthenticatedAccount(request);

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

    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png");

    @PutMapping("/edit/photo")
    public ResponseEntity<String> updatePhoto(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try{
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Nenhum arquivo enviado.");
            }

            String contentType = file.getContentType();

            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Tipo de arquivo não suportado. Apenas JPEG e PNG são permitidos.");
            }

            byte[] fileBytes = file.getBytes();

            Account account = getAuthenticatedAccount(request);  // Método que você já tem no seu controlador
            if (account == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuário não autenticado ou conta desativada.");
            }

            // 5. Atualiza a foto da conta
            // Neste exemplo, vamos armazenar o conteúdo como Base64. Outra abordagem seria salvar o arquivo em um sistema de arquivos e armazenar o caminho.
            String photoBase64 = Base64.getEncoder().encodeToString(fileBytes);  // Converte para base64

            account.setPhoto(photoBase64);  // Atualiza a foto da conta

            // 6. Salva a conta com a nova foto no banco de dados
            accountRepository.save(account);

            return ResponseEntity.ok("Foto atualizada com sucesso. Tamanho: " + fileBytes.length + " bytes.");

        }

        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo: ");
        }

    }
}
