package com.example.laptop_shop.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webConfig implements WebMvcConfigurer { // Tên class nên viết hoa: WebConfig

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình này OK, giữ nguyên
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // SỬA LẠI HOÀN TOÀN NHƯ SAU:
        registry.addMapping("/api/**") // Chỉ áp dụng cho các API dưới /api/
                .allowedOrigins("*") // <-- SỬA Ở ĐÂY: Cho phép mọi nguồn (web, mobile...)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false); // <-- SỬA Ở ĐÂY: Bắt buộc là false khi dùng allowedOrigins("*")
    }
}