package com.hyeongkyu.template.controller;

import com.hyeongkyu.template.global.common.ResponseDto;
import com.hyeongkyu.template.service.ClovaVoiceService;
import com.hyeongkyu.template.service.NewsToSpeechService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName   : com.hyeongkyu.template.controller
 * Author        : AI Assistant
 * Date          : 2026. 2. 2.
 * Description   : CLOVA Voice TTS API 컨트롤러
 */

@Slf4j
@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
@Tag(name = "CLOVA Voice TTS", description = "네이버 클라우드 CLOVA Voice를 이용한 음성 합성 API")
public class ClovaVoiceTtsController {

    private final NewsToSpeechService newsToSpeechService;
    private final ClovaVoiceService clovaVoiceService;

    /**
     * 뉴스 분석 결과를 음성으로 변환
     */
    @PostMapping("/news-analysis/{analysisId}")
    @Operation(summary = "뉴스 분석 음성 변환", 
               description = "AI 분석 결과를 CLOVA Voice로 음성(MP3)으로 변환합니다.")
    public ResponseEntity<ResponseDto<TtsResponse>> convertNewsAnalysisToSpeech(
            @PathVariable Long analysisId,
            @RequestParam(required = false, defaultValue = "jinho") String speaker) {
        
        log.info("뉴스 분석 {} 음성 변환 요청 - 화자: {}", analysisId, speaker);

        try {
            String audioFile = newsToSpeechService.convertAnalysisToSpeech(analysisId, speaker);
            
            TtsResponse response = new TtsResponse(
                    audioFile,
                    "/api/tts/audio/" + audioFile,
                    speaker,
                    "음성 변환이 완료되었습니다."
            );

            return ResponseEntity.ok((ResponseDto<TtsResponse>) ResponseDto.ok(response));
            
        } catch (IOException e) {
            log.error("TTS 변환 실패", e);
            throw new RuntimeException("음성 변환에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 커스텀 텍스트를 음성으로 변환
     */
    @PostMapping("/custom")
    @Operation(summary = "커스텀 텍스트 음성 변환", 
               description = "임의의 텍스트를 CLOVA Voice로 음성으로 변환합니다.")
    public ResponseEntity<ResponseDto<TtsResponse>> convertCustomTextToSpeech(
            @RequestBody CustomTtsRequest request) {
        
        log.info("커스텀 텍스트 음성 변환 요청 - 텍스트 길이: {}, 화자: {}", 
                request.text().length(), request.speaker());

        try {
            // 기본값 설정
            String speaker = request.speaker() != null ? request.speaker() : "jinho";
            int speed = request.speed() != null ? request.speed() : 0;
            int pitch = request.pitch() != null ? request.pitch() : 0;
            int volume = request.volume() != null ? request.volume() : 0;

            String audioFile = newsToSpeechService.convertCustomTextToSpeech(
                    request.text(), speaker, speed, pitch, volume
            );
            
            TtsResponse response = new TtsResponse(
                    audioFile,
                    "/api/tts/audio/" + audioFile,
                    speaker,
                    "음성 변환이 완료되었습니다."
            );

            return ResponseEntity.ok((ResponseDto<TtsResponse>) ResponseDto.ok(response));
            
        } catch (IOException e) {
            log.error("TTS 변환 실패", e);
            throw new RuntimeException("음성 변환에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 생성된 오디오 파일 다운로드/재생
     */
    @GetMapping("/audio/{filename}")
    @Operation(summary = "음성 파일 다운로드", 
               description = "생성된 MP3 파일을 다운로드하거나 재생합니다.")
    public ResponseEntity<Resource> downloadAudio(@PathVariable String filename) {
        log.info("오디오 파일 요청: {}", filename);

        try {
            Path audioPath = Paths.get("audio").resolve(filename);
            Resource resource = new FileSystemResource(audioPath);

            if (!resource.exists()) {
                log.error("파일을 찾을 수 없음: {}", filename);
                throw new RuntimeException("파일을 찾을 수 없습니다: " + filename);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("오디오 파일 다운로드 실패", e);
            throw new RuntimeException("파일 다운로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 지원하는 화자(Speaker) 목록 조회
     */
    @GetMapping("/speakers")
    @Operation(summary = "화자 목록 조회", 
               description = "CLOVA Voice에서 지원하는 화자 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<SpeakerInfo>>> getSpeakers() {
        log.info("화자 목록 조회 요청");

        List<SpeakerInfo> speakers = Arrays.stream(ClovaVoiceService.Speaker.values())
                .map(speaker -> new SpeakerInfo(
                        speaker.getCode(),
                        speaker.getLanguage(),
                        speaker.getGender(),
                        speaker.getDescription()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok((ResponseDto<List<SpeakerInfo>>) ResponseDto.ok(speakers));
    }

    /**
     * TTS 응답 DTO
     */
    public record TtsResponse(
            String filename,
            String downloadUrl,
            String speaker,
            String message
    ) {}

    /**
     * 커스텀 TTS 요청 DTO
     */
    public record CustomTtsRequest(
            String text,
            String speaker,
            Integer speed,   // -5 ~ 5
            Integer pitch,   // -5 ~ 5
            Integer volume   // -5 ~ 5
    ) {}

    /**
     * 화자 정보 DTO
     */
    public record SpeakerInfo(
            String code,
            String language,
            String gender,
            String description
    ) {}

}
