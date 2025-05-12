package com.books.api.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class Config {
    private final int cookieMaxAge = 48; //Tempo de vida do cookie em horas
    private final int tokenMaxAge = 48; // Tempo de vida do token JWT em horas
    private final String secretKey = "xcmzyCqUEFvjq1dU6hHje4slO9mI0A0K8RIcVddJklY=y44fn898gt308htv32ht4108h32gd"; // Chave secreta para geração de tokens JWT ou similares
    private final String uploadDir = "./uploads/photo"; // Diretório local para salvar arquivos enviados
    private final String uploadUrl = "/api/account/photo"; // URL/rota base para acesso aos arquivos enviados
    private final String appName = "Book''s Vanilla"; // Nome do sistema para exibição em páginas
    private final String defaultUserPhoto = "https://randomuser.me/api/portraits/lego/1.jpg"; // URL padrão para imagem de perfil de usuário
    private final List<String> supportedPhotoFormat = List.of("image/jpeg", "image/png");  // Tipos de imagens suportados no upload da foto
    private final int maxPhotoSizeMb = 10; // Tamanho máximo da foto de upload em MB (mega bytes)
    private final String defaultLang = "pt-BR"; // Idioma padrão da interface
    private final boolean maintenanceMode = false; // Se true, exibe página de manutenção para todos os usuários
    private final String apiVersion = "v1"; // Versão atual da API
    private final String supportEmail = "suporte@books.com"; // E-mail de contato do suporte técnico
    private final boolean httpsOn = false; // true somente se estiver usando HTTP. Afeta os cookies.
}
