package com.books.api.util;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String status;
    private String code;
    private String message;
    private Map<String, Object> data;

    // Sucesso sem data
    public static Map<String, Object> success(String code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "success");
        result.put("code", code);
        result.put("message", message);
        return result;
    }

    // Sucesso com data
    public static Map<String, Object> success(
            String code,
            String message,
            Object data
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "success");
        result.put("code", code);
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    // Erro
    public static Map<String, Object> error(String code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "error");
        result.put("code", code);
        result.put("message", message);
        return result;
    }
}