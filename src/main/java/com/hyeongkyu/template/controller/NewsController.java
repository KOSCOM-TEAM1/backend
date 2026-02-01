package com.hyeongkyu.template.controller;

import com.hyeongkyu.template.domain.entity.News;
import com.hyeongkyu.template.global.common.ResponseDto;
import com.hyeongkyu.template.repository.NewsAiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName   : com.hyeongkyu.template.controller
 * Author        : AI Assistant
 * Date          : 2026. 2. 1.
 * Description   : 뉴스 관리 컨트롤러 (테스트용)
 */

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Tag(name = "뉴스 관리", description = "뉴스 CRUD API (테스트용)")
public class NewsController {

    private final NewsAiRepository newsAiRepository;

    /**
     * 테스트용 뉴스 생성
     */
    @PostMapping
    @Operation(summary = "뉴스 생성", description = "테스트용 뉴스를 생성합니다.")
    public ResponseEntity<ResponseDto<News>> createNews(@RequestBody CreateNewsRequest request) {
        News news = News.builder()
                .title(request.title())
                .content(request.content())
                .source(request.source())
                .url(request.url())
                .publishedAt(request.publishedAt() != null ? request.publishedAt() : LocalDateTime.now())
                .relatedStockIds(request.relatedStockIds())
                .isAnalyzed(false)
                .build();

        news = newsAiRepository.save(news);
        
        log.info("뉴스 생성됨: {}", news.getId());

        return ResponseEntity.ok((ResponseDto<News>) ResponseDto.ok(news));
    }

    /**
     * 모든 뉴스 조회
     */
    @GetMapping
    @Operation(summary = "전체 뉴스 조회", description = "저장된 모든 뉴스를 조회합니다.")
    public ResponseEntity<ResponseDto<List<News>>> getAllNews() {
        List<News> newsList = newsAiRepository.findAll();
        
        return ResponseEntity.ok((ResponseDto<List<News>>) ResponseDto.ok(newsList));
    }

    /**
     * 미분석 뉴스 조회
     */
    @GetMapping("/unanalyzed")
    @Operation(summary = "미분석 뉴스 조회", description = "아직 AI 분석이 안 된 뉴스만 조회합니다.")
    public ResponseEntity<ResponseDto<List<News>>> getUnanalyzedNews() {
        List<News> newsList = newsAiRepository.findByIsAnalyzedFalse();
        
        return ResponseEntity.ok((ResponseDto<List<News>>) ResponseDto.ok(newsList));
    }

    /**
     * 샘플 뉴스 데이터 생성 (테스트용)
     */
    @PostMapping("/sample")
    @Operation(summary = "샘플 뉴스 생성", description = "테스트용 샘플 뉴스 데이터를 생성합니다.")
    public ResponseEntity<ResponseDto<List<News>>> createSampleNews() {
        LocalDateTime lastNight = LocalDateTime.now().minusHours(10);
        
        News news1 = News.builder()
                .title("삼성전자, 신규 반도체 공장 건설 발표")
                .content("삼성전자가 경기도 평택에 20조 원 규모의 차세대 반도체 공장 건설을 발표했다. "
                        + "이번 투자는 3나노 공정 기술을 적용한 최첨단 파운드리 생산 시설로, "
                        + "2027년 완공을 목표로 한다. 업계에서는 이번 투자가 글로벌 반도체 시장에서 "
                        + "삼성전자의 입지를 더욱 강화할 것으로 전망하고 있다.")
                .source("한국경제")
                .url("https://example.com/news/1")
                .publishedAt(lastNight)
                .relatedStockIds("1")
                .isAnalyzed(false)
                .build();

        News news2 = News.builder()
                .title("테슬라, 전기차 가격 10% 인하 단행")
                .content("테슬라가 모든 전기차 모델의 가격을 평균 10% 인하한다고 발표했다. "
                        + "이는 경쟁 심화와 수요 둔화에 대응하기 위한 조치로 분석된다. "
                        + "전문가들은 이번 가격 인하가 국내 전기차 시장에도 영향을 미쳐 "
                        + "현대차, 기아 등 국내 업체들의 가격 전략 수정이 불가피할 것으로 보고 있다.")
                .source("연합뉴스")
                .url("https://example.com/news/2")
                .publishedAt(lastNight.plusHours(2))
                .relatedStockIds("2,3")
                .isAnalyzed(false)
                .build();

        News news3 = News.builder()
                .title("미국 연준, 기준금리 0.25%p 인상")
                .content("미국 연방준비제도(Fed)가 기준금리를 0.25%p 인상했다. "
                        + "이는 인플레이션 압력이 여전히 높다는 판단에 따른 것으로, "
                        + "시장에서는 추가 금리 인상 가능성에 대한 우려가 커지고 있다. "
                        + "국내 증시는 이번 결정에 따라 외국인 자금 이탈 우려로 하락 압력을 받을 것으로 예상된다.")
                .source("매일경제")
                .url("https://example.com/news/3")
                .publishedAt(lastNight.plusHours(5))
                .relatedStockIds("")
                .isAnalyzed(false)
                .build();

        List<News> savedNews = newsAiRepository.saveAll(List.of(news1, news2, news3));
        
        log.info("샘플 뉴스 {}개 생성됨", savedNews.size());

        return ResponseEntity.ok((ResponseDto<List<News>>) ResponseDto.ok(savedNews));
    }

    public record CreateNewsRequest(
            String title,
            String content,
            String source,
            String url,
            LocalDateTime publishedAt,
            String relatedStockIds
    ) {}

}
