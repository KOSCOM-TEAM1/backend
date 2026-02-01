package com.hyeongkyu.template.domain.dto.response;

import java.time.LocalDateTime;

/**
 * packageName   : com.hyeongkyu.template.domain.dto.response
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : AI 뉴스 분석 응답 DTO
 */

public record NewsAnalysisResponse(
        Long analysisId,
        Long newsId,
        String newsTitle,
        String newsSummary,
        LocalDateTime newsPublishedAt,
        String summary,
        String impactAnalysis,
        String similarCases,
        String similarNewsIds,
        String aiModel,
        Double confidenceScore,
        LocalDateTime analyzedAt
) {
}
