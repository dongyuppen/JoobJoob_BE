package org.example.joobjoob.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // ✅ 1. 와일드카드(*) 대신 정확한 프론트엔드 주소를 지정합니다.
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // ✅ 2. 인증 정보(JWT 토큰 등)를 포함한 요청을 허용합니다.
                .allowCredentials(true)
                .maxAge(3600);
    }
}