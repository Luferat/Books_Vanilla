package com.books.api.service;

import com.books.api.model.Config;
import com.books.api.repository.ConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository repository;
    private Map<String, String> map;

    @PostConstruct
    public void init() {
        List<Config> list = repository.findAll();
        this.map = new HashMap<>();

        // Se não houver configs no banco, cria padrões
        if (list.isEmpty()) {
            String defaultSecret = UUID.randomUUID().toString().replace("-", "") + "XYZ1234567890";
            String defaultHours = "12";

            repository.saveAll(List.of(
                    new Config(null, "secretKey", defaultSecret, "Chave secreta JWT"),
                    new Config(null, "tokenMaxAge", defaultHours, "Validade do token em horas")
            ));

            this.map.put("secretKey", defaultSecret);
            this.map.put("tokenMaxAge", defaultHours);
        } else {
            for (Config config : list) {
                this.map.put(config.getVarName(), config.getVarValue());
            }
        }
    }

    public String get(String name) {
        return map.get(name);
    }

    public int getInt(String name) {
        return Integer.parseInt(map.get(name));
    }
}
