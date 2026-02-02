package com.hyeongkyu.template.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * packageName   : com.hyeongkyu.template.service
 * Author        : AI Assistant
 * Date          : 2026. 2. 2.
 * Description   : 네이버 클라우드 CLOVA Voice TTS 서비스
 */

@Slf4j
@Service
public class ClovaVoiceService {

    private final RestTemplate restTemplate;

    @Value("${naver.clova.client-id}")
    private String clientId;

    @Value("${naver.clova.client-secret}")
    private String clientSecret;

    @Value("${naver.clova.api-url}")
    private String apiUrl;

    public ClovaVoiceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 텍스트를 음성(MP3)으로 변환
     * 
     * @param text 변환할 텍스트
     * @param speaker 화자 (기본값: "nara")
     * @param speed 속도 -5~5 (기본값: 0)
     * @param pitch 높낮이 -5~5 (기본값: 0)
     * @param volume 음량 -5~5 (기본값: 0)
     * @return 생성된 MP3 파일명
     */
    public String textToSpeech(String text, String speaker, int speed, int pitch, int volume) throws IOException {
        log.info("CLOVA Voice TTS 요청 - 텍스트 길이: {}, 화자: {}", text.length(), speaker);

        // 1. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-api-key-id", clientId);           // Client ID
        headers.set("x-ncp-apigw-api-key", clientSecret);          // Client Secret
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 2. 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("speaker", speaker);
        params.add("text", text);
        params.add("speed", String.valueOf(speed));
        params.add("pitch", String.valueOf(pitch));
        params.add("volume", String.valueOf(volume));
        params.add("format", "mp3");

        // 3. HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            // 4. API 호출
            log.info("CLOVA Voice API 호출 - URL: {}", apiUrl);
            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    apiUrl,
                    requestEntity,
                    byte[].class
            );

            // 5. 응답 확인
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                byte[] audioData = response.getBody();
                log.info("TTS 음성 데이터 수신 완료 - 크기: {} bytes", audioData.length);

                // 6. MP3 파일 저장
                String filename = saveAudioFile(audioData);
                log.info("TTS MP3 파일 저장 완료 - 파일명: {}", filename);

                return filename;
            } else {
                log.error("CLOVA Voice API 응답 오류 - 상태 코드: {}", response.getStatusCode());
                throw new RuntimeException("TTS 생성 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("CLOVA Voice API 호출 중 오류 발생", e);
            throw new RuntimeException("TTS 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 기본 설정으로 TTS 생성
     * 화자: nara (여성 차분한 목소리)
     * 속도, 높낮이, 음량: 0 (기본값)
     */
    public String textToSpeech(String text) throws IOException {
        return textToSpeech(text, "nara", 0, 0, 0);
    }

    /**
     * 화자만 지정하여 TTS 생성
     */
    public String textToSpeech(String text, String speaker) throws IOException {
        return textToSpeech(text, speaker, 0, 0, 0);
    }

    /**
     * MP3 파일을 서버에 저장
     * 
     * @param audioData MP3 바이너리 데이터
     * @return 저장된 파일명
     */
    private String saveAudioFile(byte[] audioData) throws IOException {
        // 1. 저장 디렉토리 생성
        Path audioDir = Paths.get("audio");
        if (!Files.exists(audioDir)) {
            Files.createDirectories(audioDir);
            log.info("오디오 디렉토리 생성: {}", audioDir.toAbsolutePath());
        }

        // 2. 파일명 생성 (UUID 사용)
        String filename = "clova_" + UUID.randomUUID().toString() + ".mp3";
        Path filepath = audioDir.resolve(filename);

        // 3. 파일 저장
        Files.write(filepath, audioData);
        log.info("MP3 파일 저장 완료 - 경로: {}, 크기: {} bytes", 
                filepath.toAbsolutePath(), audioData.length);

        return filename;
    }

    /**
     * CLOVA Voice Premium에서 지원하는 화자(Speaker) 목록
     */
    public enum Speaker {
        // 한국어 화자
        NARA("nara", "한국어", "여성", "차분한 목소리"),
        JINHO("jinho", "한국어", "남성", "뉴스 앵커 스타일"),
        CLARA("clara", "한국어", "여성", "밝은 목소리"),
        MATT("matt", "한국어", "남성", "차분한 목소리"),
        SHINJI("shinji", "한국어", "남성", "은은한 목소리"),
        DINNA("dinna", "한국어", "여성", "자연스러운 목소리"),
        
        // 영어 화자
        CLARA_EN("clara_en", "영어", "여성", "밝은 목소리"),
        MATT_EN("matt_en", "영어", "남성", "차분한 목소리"),
        
        // 일본어 화자
        YURI("yuri", "일본어", "여성", "표준 목소리"),
        SHINJI_JP("shinji_jp", "일본어", "남성", "표준 목소리"),
        
        // 중국어 화자
        MEIMEI("meimei", "중국어", "여성", "표준 목소리"),
        LIANGLIANG("liangliang", "중국어", "남성", "표준 목소리"),
        
        // 스페인어 화자
        CARMEN("carmen", "스페인어", "여성", "표준 목소리"),
        JOSE("jose", "스페인어", "남성", "표준 목소리");

        private final String code;
        private final String language;
        private final String gender;
        private final String description;

        Speaker(String code, String language, String gender, String description) {
            this.code = code;
            this.language = language;
            this.gender = gender;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getLanguage() {
            return language;
        }

        public String getGender() {
            return gender;
        }

        public String getDescription() {
            return description;
        }
    }

}
