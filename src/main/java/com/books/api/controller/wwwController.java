package com.books.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class wwwController {

    @GetMapping("/hello")
    public String hello() throws IOException {
        return getPage("src/main/resources/static/index.html");
    }

    public static String getPage(String path) throws IOException {
        Path filePath = Paths.get(path);
        return Files.readString(filePath);
    }
}
