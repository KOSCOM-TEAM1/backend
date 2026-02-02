package com.hyeongkyu.template.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * packageName   : com.hyeongkyu.template.global.config
 * Author        : AI Assistant
 * Date          : 2026. 2. 2.
 * Description   : RestTemplate 설정 (외부 API 호출용)
 */

@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate 빈 생성
     * - 외부 API (CLOVA Voice 등) 호출에 사용
     * - 타임아웃 설정: 연결 5초, 읽기 30초 (TTS는 시간이 걸릴 수 있음)
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 연결 타임아웃: 5초
        factory.setReadTimeout(30000);    // 읽기 타임아웃: 30초 (TTS 생성 시간 고려)
        
        return new RestTemplate(factory);
    }

}
