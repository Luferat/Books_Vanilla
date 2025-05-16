package com.books.api.controller;

import com.books.api.config.Config;
import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final Config config;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @GetMapping
    public ResponseEntity<?> getStatus(HttpServletRequest request) {
        // Verifica se o usuário está autenticado
        Account loggedUser = jwtUtil.getLoggedUser(request, accountRepository);
        if (loggedUser == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("401", "Não autenticado ou token inválido."));
        }

        // Verifica se o usuário tem permissão (ADMIN ou EMPLOYEE)
        if (loggedUser.getRole() != Account.Role.ADMIN) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("403", "Acesso negado. Requer role de ADMIN ou OPERATOR."));
        }

        // Cria o mapa com as configurações solicitadas
        Map<String, Object> configData = new LinkedHashMap<>();
        configData.put("appName", config.getAppName());
        configData.put("defaultUserPhoto", config.getDefaultUserPhoto());
        configData.put("supportedPhotoFormat", config.getSupportedPhotoFormat());
        configData.put("defaultLang", config.getDefaultLang());
        configData.put("maintenanceMode", config.isMaintenanceMode());
        configData.put("apiVersion", config.getApiVersion());
        configData.put("supportEmail", config.getSupportEmail());

        return ResponseEntity.ok(ApiResponse.success(
                "200",
                "System configuration retrieved successfully",
                configData
        ));
    }
}