package com.books.api.util;

import com.books.api.model.Account;
import com.books.api.service.ConfigService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final ConfigService config;

    public void cookieToken(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(config.getInt("cookieMaxAge") * 3600);
        response.addCookie(cookie);
    }

    public void cookieUser(Account user, HttpServletResponse response) {
        String json = String.format("{\"id\":%d,\"name\":\"%s\",\"photo\":\"%s\",\"role\":\"%s\"}",
                user.getId(),
                user.getName(),
                user.getPhoto(),
                user.getRole()
        );
        String encoded = URLEncoder.encode(json, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie("userdata", encoded);
        cookie.setPath("/");
        cookie.setHttpOnly(false); // acessível via JS
        cookie.setMaxAge(config.getInt("cookieMaxAge") * 3600);
        response.addCookie(cookie);
    }

    public void deleteCookies(HttpServletResponse response) {
        Cookie token = new Cookie("token", null);
        token.setPath("/");
        token.setHttpOnly(true);
        token.setMaxAge(0);

        Cookie user = new Cookie("userdata", null);
        user.setPath("/");
        user.setHttpOnly(false);
        user.setMaxAge(0);

        response.addCookie(token);
        response.addCookie(user);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
