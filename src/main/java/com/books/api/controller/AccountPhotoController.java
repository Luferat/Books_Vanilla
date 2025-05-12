package com.books.api.controller;

import com.books.api.config.Config;
import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountPhotoController {

    private final AccountRepository accountRepo;
    private final JwtUtil jwt;
    private final Config config;

    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(@RequestParam("photo") MultipartFile photoFile, HttpServletRequest request) {

        Account loggedUser = jwt.getLoggedUser(request, accountRepo);

        if (loggedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("401", "Não autenticado."));
        }

        if (photoFile.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "Nenhum arquivo de foto foi enviado."));
        }

        // Verifica se a conta do usuário está ativa
        if (loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("403", "Sua conta está inativa."));
        }

        // Validação do tipo de arquivo
        String contentType = photoFile.getContentType();
        if (contentType == null || !config.getSupportedPhotoFormat().contains(contentType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("400",
                    "Formato de foto não suportado. Os formatos suportados são: " + String.join(", ", config.getSupportedPhotoFormat())));
        }

        // Validação do tamanho do arquivo
        long maxFileSize = (long) config.getMaxPhotoSizeMb() * 1024 * 1024; // Converte MB para bytes
        if (photoFile.getSize() > maxFileSize) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(ApiResponse.error("413",
                    "O tamanho da foto excede o limite de " + config.getMaxPhotoSizeMb() + "MB."));
        }

        try {
            String photoUrl = savePhotoAndGetUrl(photoFile, loggedUser.getId());
            loggedUser.setPhoto(photoUrl);
            accountRepo.save(loggedUser);

            Map<String, String> responseData = Map.of("photoUrl", photoUrl);
            return ResponseEntity.ok(ApiResponse.success("200", "Foto de perfil atualizada com sucesso.", responseData));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("500", "Erro ao processar o arquivo de foto."));
        }
    }

    private String savePhotoAndGetUrl(MultipartFile photoFile, Long userId) throws IOException {
        String fileName = "user_" + userId + "_" + System.currentTimeMillis() + "_" + Objects.requireNonNull(photoFile.getOriginalFilename()).replaceAll("[^a-zA-Z0-9._-]", "");
        Path uploadPath = Paths.get(config.getUploadDir());

        // Cria o diretório de upload se não existir
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retorna a URL para acessar a foto (usando a URL base configurada)
        return config.getUploadUrl() + "/" + fileName;
    }
}