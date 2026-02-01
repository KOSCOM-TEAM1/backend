package com.hyeongkyu.template.global.config;

import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName   : com.hyeongkyu.template.global.config
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : OpenAI API 설정
 */

@Configuration
public class OpenAiConfig {

    @Value("${openai.api-key}")
    private String apiKey;

    @Bean
    public OpenAiService openAiService() {
        // 타임아웃을 60초로 설정 (AI 응답이 오래 걸릴 수 있음)
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }

}
