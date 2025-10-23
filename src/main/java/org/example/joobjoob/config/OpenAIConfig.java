package org.example.joobjoob.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api-key}")
    private String apiKey;

    @Bean
    public OpenAiService openAiService() {
        // 필요시 타임아웃을 늘리세요 (기본값 10초)
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }
}