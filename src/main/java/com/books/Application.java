package com.books;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@GetMapping("/hello")
	public String hello() throws IOException {
		return getPage("src/main/resources/static/index.html");
	}


	public static String getPage(String path) throws IOException {
		Path filePath = Paths.get(path);
		return Files.readString(filePath);
	}
}
