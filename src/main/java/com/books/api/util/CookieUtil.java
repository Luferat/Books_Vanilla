package com.books.api.util;

import com.books.api.config.Config;
import com.books.api.model.Account;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final Config config;

    public void cookieToken(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(config.isHttpsOn());
        cookie.setMaxAge(config.getCookieMaxAge() * 3600);
        response.addCookie(cookie);
    }

    public void cookieUser(Account user, String token, HttpServletResponse response) {
        String json = String.format("{\"id\":%d,\"name\":\"%s\",\"photo\":\"%s\",\"role\":\"%s\", \"token\":\"%s\"}",
                user.getId(),
                user.getName(),
                user.getPhoto(),
                user.getRole(),
                token
        );
        String encoded = URLEncoder.encode(json, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie("userdata", encoded);
        cookie.setPath("/");
        cookie.setHttpOnly(false); // acessível via JS
        cookie.setSecure(config.isHttpsOn());
        cookie.setMaxAge(config.getCookieMaxAge() * 3600);
        response.addCookie(cookie);
    }

    public void removeCookie(String name, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .httpOnly("token".equals(name))
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
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
