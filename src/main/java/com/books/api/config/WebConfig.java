package com.books.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Config config;

    public WebConfig(Config config) {
        this.config = config;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(config.getUploadDir());
        String uploadLocation = "file:" + uploadPath.toAbsolutePath() + "/";
        registry.addResourceHandler(config.getUploadUrl() + "/**")
                .addResourceLocations(uploadLocation);
    }
}