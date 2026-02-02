package com.hyeongkyu.template.service;

import com.hyeongkyu.template.domain.entity.NewsAnalysis;
import com.hyeongkyu.template.repository.NewsAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * packageName   : com.hyeongkyu.template.service
 * Author        : AI Assistant
 * Date          : 2026. 2. 2.
 * Description   : 뉴스 분석 결과를 음성으로 변환하는 서비스
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsToSpeechService {

    private final ClovaVoiceService clovaVoiceService;
    private final NewsAnalysisResultRepository newsAnalysisResultRepository;

    /**
     * 뉴스 분석 결과를 음성으로 변환
     * 
     * @param analysisId 분석 결과 ID
     * @param speaker 화자 (기본값: jinho - 뉴스 앵커)
     * @return MP3 파일명
     */
    public String convertAnalysisToSpeech(Long analysisId, String speaker) throws IOException {
        log.info("뉴스 분석 {} 음성 변환 시작 - 화자: {}", analysisId, speaker);

        // 1. 분석 결과 조회
        NewsAnalysis analysis = newsAnalysisResultRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("분석 결과를 찾을 수 없습니다: " + analysisId));

        // 2. 음성 스크립트 생성
        String script = buildSpeechScript(analysis);
        log.info("생성된 스크립트 길이: {} 자", script.length());

        // 3. CLOVA Voice TTS 변환
        String audioFile = clovaVoiceService.textToSpeech(script, speaker);

        log.info("음성 변환 완료 - 파일: {}", audioFile);
        return audioFile;
    }

    /**
     * 기본 화자(jinho - 뉴스 앵커)로 변환
     */
    public String convertAnalysisToSpeech(Long analysisId) throws IOException {
        return convertAnalysisToSpeech(analysisId, "jinho");
    }

    /**
     * 음성 스크립트 생성
     * AI 분석 결과를 자연스럽게 읽을 수 있는 텍스트로 변환
     */
    private String buildSpeechScript(NewsAnalysis analysis) {
        StringBuilder script = new StringBuilder();

        // 인트로
        script.append("뉴스 분석 결과를 말씀드리겠습니다. ");
        script.append("\n\n");

        // 1. 요약
        if (analysis.getSummary() != null && !analysis.getSummary().isEmpty()) {
            script.append("먼저 뉴스 요약입니다. ");
            script.append(cleanTextForTts(analysis.getSummary()));
            script.append("\n\n");
        }

        // 2. 핵심 내용
        if (analysis.getImpactAnalysis() != null && !analysis.getImpactAnalysis().isEmpty()) {
            script.append("핵심 내용입니다. ");
            script.append(cleanTextForTts(analysis.getImpactAnalysis()));
            script.append("\n\n");
        }

        // 3. 유사 과거 사례
        if (analysis.getSimilarCases() != null && !analysis.getSimilarCases().isEmpty()) {
            script.append("유사한 과거 사례입니다. ");
            script.append(cleanTextForTts(analysis.getSimilarCases()));
            script.append("\n\n");
        }

        // 아웃트로
        script.append("이상 뉴스 분석을 마치겠습니다.");

        return script.toString();
    }

    /**
     * TTS에 적합하도록 텍스트 정제
     * - 마크다운 문법 제거
     * - 특수문자 처리
     * - 자연스러운 문장으로 변환
     */
    private String cleanTextForTts(String text) {
        if (text == null) {
            return "";
        }

        return text
                // 마크다운 헤더 제거 (### 등)
                .replaceAll("#{1,6}\\s*", "")
                // bullet point 제거하고 쉼표로 대체
                .replaceAll("^[\\-\\*]\\s+", "")
                .replaceAll("\\n[\\-\\*]\\s+", ", ")
                // 숫자 리스트 제거 (1. 2. 3. 등)
                .replaceAll("\\d+\\.\\s+", "")
                // 볼드, 이탤릭 마크다운 제거
                .replaceAll("[\\*_]{1,2}([^\\*_]+)[\\*_]{1,2}", "$1")
                // 괄호 안 내용을 자연스럽게 읽을 수 있도록 변경
                .replaceAll("\\(([^)]+)\\)", ", $1, ")
                // 연속된 공백/줄바꿈 제거
                .replaceAll("\\s+", " ")
                // 앞뒤 공백 제거
                .trim();
    }

    /**
     * 커스텀 텍스트를 음성으로 변환
     * (뉴스 분석이 아닌 임의의 텍스트)
     */
    public String convertCustomTextToSpeech(String text, String speaker, int speed, int pitch, int volume) 
            throws IOException {
        log.info("커스텀 텍스트 음성 변환 - 길이: {}, 화자: {}", text.length(), speaker);
        return clovaVoiceService.textToSpeech(text, speaker, speed, pitch, volume);
    }

    /**
     * 커스텀 텍스트를 기본 설정으로 음성 변환
     */
    public String convertCustomTextToSpeech(String text) throws IOException {
        return clovaVoiceService.textToSpeech(text);
    }

}
