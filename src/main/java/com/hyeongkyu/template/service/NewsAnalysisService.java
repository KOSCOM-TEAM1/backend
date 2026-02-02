package com.hyeongkyu.template.service;

import com.hyeongkyu.template.domain.dto.response.NewsAnalysisResponse;
import com.hyeongkyu.template.domain.entity.News;
import com.hyeongkyu.template.domain.entity.NewsAnalysis;
import com.hyeongkyu.template.repository.NewsAiRepository;
import com.hyeongkyu.template.repository.NewsAnalysisResultRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName   : com.hyeongkyu.template.service
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : AI 뉴스 분석 서비스
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsAnalysisService {

    private final OpenAiService openAiService;
    private final NewsAiRepository newsAiRepository;
    private final NewsAnalysisResultRepository newsAnalysisResultRepository;

    /**
     * 모든 미분석 뉴스를 AI로 분석
     */
    @Transactional
    public List<NewsAnalysisResponse> analyzeAllUnanalyzedNews() {
        log.info("미분석 뉴스 분석 시작");

        // 1. 미분석 뉴스 가져오기
        List<News> newsToAnalyze = newsAiRepository.findByIsAnalyzedFalse();

        if (newsToAnalyze.isEmpty()) {
            log.info("분석할 뉴스가 없습니다.");
            return new ArrayList<>();
        }

        log.info("분석할 뉴스 개수: {}", newsToAnalyze.size());

        // 2. 각 뉴스에 대해 AI 분석 수행
        List<NewsAnalysisResponse> results = new ArrayList<>();
        for (News news : newsToAnalyze) {
            try {
                NewsAnalysisResponse analysis = analyzeNewsWithAI(news);
                results.add(analysis);

                // 뉴스를 분석 완료로 표시
                news.setIsAnalyzed(true);
                newsAiRepository.save(news);

            } catch (Exception e) {
                log.error("뉴스 {} 분석 중 오류 발생: {}", news.getId(), e.getMessage(), e);
            }
        }

        log.info("분석 완료: 총 {}개의 뉴스 분석됨", results.size());
        return results;
    }

    /**
     * 특정 뉴스 ID를 AI로 분석
     */
    @Transactional
    public NewsAnalysisResponse analyzeSingleNews(Long newsId) {
        log.info("뉴스 {} 분석 시작", newsId);

        News news = newsAiRepository.findById(newsId)
                                    .orElseThrow(
                                        () -> new RuntimeException("뉴스를 찾을 수 없습니다: " + newsId));

        NewsAnalysisResponse analysis = analyzeNewsWithAI(news);

        // 뉴스를 분석 완료로 표시
        news.setIsAnalyzed(true);
        newsAiRepository.save(news);

        log.info("뉴스 {} 분석 완료", newsId);
        return analysis;
    }

    /**
     * AI를 사용하여 단일 뉴스 분석
     */
    private NewsAnalysisResponse analyzeNewsWithAI(News news) {
        log.info("뉴스 ID {} 분석 시작", news.getId());

        // 1. AI에게 보낼 프롬프트 생성
        String prompt = buildAnalysisPrompt(news);

        // 2. OpenAI API 호출
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(),
            "당신은 증권 뉴스 분석 전문가입니다. 뉴스를 분석하고 투자자에게 유용한 정보를 제공합니다."));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

        ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                                                                 .model(
                                                                     "gpt-4")  // gpt-4 또는 gpt-3.5-turbo 사용 가능
                                                                 .messages(messages)
                                                                 .temperature(0.7)
                                                                 .maxTokens(2000)
                                                                 .build();

        String aiResponse = openAiService.createChatCompletion(chatRequest)
                                         .getChoices()
                                         .get(0)
                                         .getMessage()
                                         .getContent();

        log.info("AI 응답 받음: {}", aiResponse.substring(0, Math.min(100, aiResponse.length())));

        // 3. AI 응답 파싱
        AnalysisResult parsedResult = parseAIResponse(aiResponse);

        // 4. 유사 뉴스 검색 (과거 뉴스에서 유사한 것 찾기)
        List<News> similarNews = findSimilarNews(news);
        String similarNewsIds = similarNews.stream()
                                           .map(n -> n.getId()
                                                      .toString())
                                           .collect(Collectors.joining(","));

        // 5. DB에 저장
        NewsAnalysis analysis = NewsAnalysis.builder()
                                            .newsId(news.getId())
                                            .userId(1L)  // 기본 사용자 ID (필요하면 파라미터로 받을 수 있음)
                                            .summary(parsedResult.summary)
                                            .impactAnalysis(parsedResult.keyPoints)
                                            .similarCases(parsedResult.similarCases)
                                            .similarNewsIds(similarNewsIds)
                                            .aiModel("gpt-4")
                                            .confidenceScore(0.85)
                                            .build();

        analysis = newsAnalysisResultRepository.save(analysis);

        // 6. 응답 DTO 생성
        return new NewsAnalysisResponse(
            analysis.getId(),
            news.getId(),
            news.getTitle(),
            news.getContent()
                .substring(0, Math.min(200, news.getContent()
                                                .length())),
            news.getPublishedAt(),
            analysis.getSummary(),
            analysis.getImpactAnalysis(),
            analysis.getSimilarCases(),
            similarNewsIds,
            analysis.getAiModel(),
            analysis.getConfidenceScore(),
            analysis.getCreatedAt()
        );
    }

    /**
     * AI 분석용 프롬프트 생성 (심플 버전)
     */
    private String buildAnalysisPrompt(News news) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 증권 뉴스를 분석해주세요:\n\n");
        prompt.append("=== 뉴스 정보 ===\n");
        prompt.append("제목: ")
              .append(news.getTitle())
              .append("\n");
        prompt.append("내용: ")
              .append(news.getContent())
              .append("\n");
        prompt.append("출처: ")
              .append(news.getSource())
              .append("\n");
        prompt.append("발행 시간: ")
              .append(news.getPublishedAt())
              .append("\n\n");

        prompt.append("다음 형식으로 분석 결과를 작성해주세요:\n\n");

        prompt.append("### 1. 뉴스 요약\n");
        prompt.append("[이 뉴스를 3-4문장으로 명확하게 요약해주세요]\n\n");

        prompt.append("### 2. 핵심 내용\n");
        prompt.append("[투자자가 꼭 알아야 할 핵심 포인트를 3-5개의 bullet point로 작성]\n");
        prompt.append("- [핵심 포인트 1]\n");
        prompt.append("- [핵심 포인트 2]\n");
        prompt.append("- [핵심 포인트 3]\n\n");

        prompt.append("### 3. 유사 과거 사례\n");
        prompt.append("[과거에 비슷한 뉴스나 사건이 있었는지 분석]\n");
        prompt.append("[유사 사례가 있다면: 사례 설명 + 당시 시장/주가 반응 + 결과]\n");
        prompt.append("[유사 사례가 없다면: '유사한 과거 사례를 찾기 어렵습니다' 라고 명시]\n");

        return prompt.toString();
    }


    /**
     * AI 응답 파싱
     */
    private AnalysisResult parseAIResponse(String aiResponse) {
        // AI 응답을 섹션별로 분리
        String summary = extractSection(aiResponse, "1. 뉴스 요약", "2. 핵심 내용");
        String keyPoints = extractSection(aiResponse, "2. 핵심 내용", "3. 유사 과거");
        String similarCases = extractSection(aiResponse, "3. 유사 과거 사례", "###END###");

        return new AnalysisResult(
            summary.trim(),
            keyPoints.trim(),
            similarCases.trim()
        );
    }

    /**
     * 텍스트에서 특정 섹션 추출
     */
    private String extractSection(String text, String startMarker, String endMarker) {
        int startIdx = text.indexOf(startMarker);
        if (startIdx == -1) {
            return "";
        }

        startIdx = text.indexOf("\n", startIdx) + 1;

        int endIdx = text.indexOf(endMarker, startIdx);
        if (endIdx == -1) {
            endIdx = text.length();
        }

        return text.substring(startIdx, endIdx)
                   .trim();
    }

    /**
     * 유사한 과거 뉴스 찾기
     * 실제로는 벡터 DB나 키워드 매칭 등을 사용
     */
    private List<News> findSimilarNews(News currentNews) {
        // 간단한 예시: 최근 7일간의 뉴스 중 3개 반환
        LocalDateTime sevenDaysAgo = LocalDateTime.now()
                                                  .minusDays(7);
        List<News> recentNews = newsAiRepository.findByPublishedAtBetween(
            sevenDaysAgo,
            currentNews.getPublishedAt()
        );

        return recentNews.stream()
                         .filter(news -> !news.getId()
                                              .equals(currentNews.getId()))
                         .limit(3)
                         .collect(Collectors.toList());
    }

    /**
     * AI 응답 파싱 결과를 담는 내부 클래스
     */
    private record AnalysisResult(
        String summary,
        String keyPoints,
        String similarCases
    ) {

    }

}
