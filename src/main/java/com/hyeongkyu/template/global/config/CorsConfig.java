package com.hyeongkyu.template.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS(Cross-Origin Resource Sharing) 설정
 * 프론트엔드( track24.vercel.app )에서 백엔드 API 호출 허용
 */
@Configuration
public class CorsConfig {

    /** 프론트엔드 배포 도메인 */
    private static final String FRONTEND_ORIGIN = "https://track24.vercel.app";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin
        config.setAllowedOrigins(List.of(
                FRONTEND_ORIGIN,
                "http://localhost:3000",   // 로컬 개발용
                "http://localhost:5173"     // Vite 등 로컬 개발용
        ));

        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 허용할 요청 헤더
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin"
        ));

        // 브라우저가 인증 정보(cookie 등)를 보낼 수 있도록
        config.setAllowCredentials(true);

        // Preflight(OPTIONS) 캐시 시간(초)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
