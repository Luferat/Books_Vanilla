package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountPhotoController {

    private final AccountRepository accountRepo;
    private final JwtUtil jwt;
    // Adicione qualquer outro serviço necessário, como um serviço de armazenamento de arquivos

    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("photo") MultipartFile photoFile,
            HttpServletRequest request) {

        Account loggedUser = jwt.getLoggedUser(request, accountRepo);

        if (loggedUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("401", "Não autenticado."));
        }

        if (photoFile.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("400", "Nenhum arquivo de foto foi enviado."));
        }

        try {
            // Aqui você processaria o arquivo de imagem:
            // - Validar o tipo de arquivo (ex: image/jpeg, image/png)
            // - Validar o tamanho do arquivo
            // - Salvar o arquivo em algum lugar (sistema de arquivos, armazenamento em nuvem, etc.)
            // - Obter a URL ou o caminho para o arquivo salvo

            String photoUrl = savePhotoAndGetUrl(photoFile, loggedUser.getId()); // Implemente este método

            // Atualizar a URL da foto no objeto Account do usuário
            loggedUser.setPhoto(photoUrl);
            accountRepo.save(loggedUser);

            Map<String, String> responseData = Map.of("photoUrl", photoUrl);
            return ResponseEntity.ok(ApiResponse.success("200", "Foto de perfil atualizada com sucesso.", responseData));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("500", "Erro ao processar o arquivo de foto."));
        }
    }

    // Implementação de um metodo para salvar a foto e obter a URL
    private String savePhotoAndGetUrl(MultipartFile photoFile, Long userId) throws IOException {
        // Exemplo básico: salvar no sistema de arquivos com um nome baseado no ID do usuário
        String fileName = "user_" + userId + "_" + photoFile.getOriginalFilename();
        // Defina o diretório onde as fotos serão salvas
        String uploadDir = "uploads/photos/";
        Path filePath = Paths.get(uploadDir, fileName);

        // Crie o diretório se não existir
        Files.createDirectories(Paths.get(uploadDir));

        // Salve o arquivo
        Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retorne a URL ou o caminho para o arquivo salvo (dependendo de como você serve os arquivos)
        return "/uploads/photos/" + fileName; // Exemplo de URL relativa
    }
}