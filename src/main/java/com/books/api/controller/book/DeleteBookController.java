package com.books.api.controller.book;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class DeleteBookController {

    @PostMapping("/delete")
    public ResponseEntity<?> deletBook(@RequestBody Map<String, String> body, HttpServletRequest request){

    }
    }
