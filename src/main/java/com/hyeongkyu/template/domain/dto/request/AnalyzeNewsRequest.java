package com.hyeongkyu.template.domain.dto.request;

import java.time.LocalDateTime;

/**
 * packageName   : com.hyeongkyu.template.domain.dto.request
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : 뉴스 분석 요청 DTO
 */

public record AnalyzeNewsRequest(
        Long userId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
