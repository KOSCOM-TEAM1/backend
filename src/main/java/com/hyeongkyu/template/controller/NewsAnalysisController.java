package com.hyeongkyu.template.controller;

import com.hyeongkyu.template.domain.dto.response.NewsAnalysisResponse;
import com.hyeongkyu.template.global.common.ResponseDto;
import com.hyeongkyu.template.service.NewsAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName   : com.hyeongkyu.template.controller
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : 뉴스 AI 분석 컨트롤러
 */

@Slf4j
@RestController
@RequestMapping("/api/news-analysis")
@RequiredArgsConstructor
@Tag(name = "뉴스 AI 분석", description = "OpenAI를 활용한 증권 뉴스 분석 API")
public class NewsAnalysisController {

    private final NewsAnalysisService newsAnalysisService;

    /**
     * 모든 미분석 뉴스를 AI로 분석
     * DB에 저장된 미분석 뉴스들을 가져와서 OpenAI로 분석
     */
    @PostMapping("/analyze-all")
    @Operation(summary = "전체 미분석 뉴스 AI 분석", 
               description = "DB에 저장된 모든 미분석 뉴스를 AI로 분석합니다. (요약 + 핵심 내용 + 유사 과거 사례)")
    public ResponseEntity<ResponseDto<List<NewsAnalysisResponse>>> analyzeAllNews() {
        
        log.info("전체 미분석 뉴스 AI 분석 요청");

        List<NewsAnalysisResponse> results = newsAnalysisService.analyzeAllUnanalyzedNews();

        return ResponseEntity.ok((ResponseDto<List<NewsAnalysisResponse>>) ResponseDto.ok(results));
    }

    /**
     * 특정 뉴스만 AI로 분석
     */
    @PostMapping("/analyze/{newsId}")
    @Operation(summary = "특정 뉴스 AI 분석", 
               description = "특정 뉴스 ID를 지정하여 AI로 분석합니다.")
    public ResponseEntity<ResponseDto<NewsAnalysisResponse>> analyzeSingleNews(
            @PathVariable Long newsId) {
        
        log.info("뉴스 {} AI 분석 요청", newsId);

        NewsAnalysisResponse result = newsAnalysisService.analyzeSingleNews(newsId);

        return ResponseEntity.ok((ResponseDto<NewsAnalysisResponse>) ResponseDto.ok(result));
    }

}
