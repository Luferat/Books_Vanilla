package com.books.api.controller.account;

import com.books.api.util.ApiResponse;
import com.books.api.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class LogoutController {

    private final CookieUtil cookieUtil;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        cookieUtil.removeCookie("token", response);
        cookieUtil.removeCookie("userdata", response);
        return ResponseEntity.ok(ApiResponse.success("200", "Logout realizado com sucesso."));
    }
}
