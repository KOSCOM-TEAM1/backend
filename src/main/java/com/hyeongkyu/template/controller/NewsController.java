package com.hyeongkyu.template.controller;

import com.hyeongkyu.template.domain.entity.News;
import com.hyeongkyu.template.global.common.ResponseDto;
import com.hyeongkyu.template.repository.NewsAiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Tag(name = "뉴스 관리", description = "뉴스 CRUD API")
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
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        List<News> newsList = new ArrayList<>();

        // 1. 삼성전자 - 4분기 실적 발표
        newsList.add(News.builder()
                .title("삼성전자, 2025년 4분기 영업이익 20조원 달성…HBM4 공급 가속화")
                .content("삼성전자는 2026년 1월 29일 공식 발표를 통해 2025년 연간 연결 기준 매출 333.6조원과 영업이익 43.6조원을 달성했다고 밝혔다. " +
                        "특히 2025년 4분기 매출은 93.8조원으로 집계돼 분기 기준 역대 최대치를 경신한 것으로 나타났으며 이는 DS 부문이 주도한 고부가 제품 판매 확대 전략이 전사 실적을 강력하게 견인한 결과로 풀이된다. " +
                        "삼성전자는 2026년 1분기부터 업계 선도 11.7Gbps 성능을 갖춘 차세대 HBM4 제품 공급을 시작할 계획이라고 밝히며 시장의 우려를 기술 리더십으로 돌파하겠다는 의지를 내비쳤다.")
                .source("이포커스")
                .url("https://www.e-focus.co.kr/news/articleView.html?idxno=3002014")
                .publishedAt(lastWeek)
                .relatedStockIds("1")
                .isAnalyzed(false)
                .build());

        // 2. SK하이닉스 - 목표주가 상향
        newsList.add(News.builder()
                .title("SK하이닉스, 주가 91만원 돌파…'황제주' 진입 코앞")
                .content("SK하이닉스가 2026년 1월 30일 오전 주가 91만원을 돌파하며 '황제주'(주가 100만원 이상) 진입을 코앞에 뒀다. 3일 전 80만원을 넘은 지 사흘 만에 다시 신기록을 세우면서, 14년 전 해당 종목을 2만원대에 매수한 투자 사례가 재조명되고 있다. " +
                        "증권사들은 SK하이닉스 목표가를 일제히 상향 조정했다. 메리츠증권이 145만원으로 가장 높게 제시했으며, 한국투자증권과 삼성증권은 130만원, 하나증권은 128만원으로 잡았다. " +
                        "모건 스탠리는 2026년 DRAM 평균 가격이 62%, NAND는 75% 상승할 것으로 전망했다.")
                .source("Econmingle")
                .url("https://econmingle.com/economy/sk-hynix-stock-surge-90-ten-year-investment/")
                .publishedAt(lastWeek.plusHours(2))
                .relatedStockIds("4")
                .isAnalyzed(false)
                .build());

        // 3. 네이버 - AI 국가 프로젝트 탈락 이후 재평가
        newsList.add(News.builder()
                .title("네이버, 2026년 안정적 이익 성장 확정...신사업 가치 주가 반영 전망")
                .content("하나증권은 네이버에 대해 2026년 안정적 이익 성장이 확정적인 상황에서 신사업 가치가 주가에 반영될 가능성이 높다고 밝혔다. 목표주가는 35만 원으로 제시했으며 투자의견은 매수로 유지했다. " +
                        "2026년 연결 기준 영업수익 13조1,464억 원, 영업이익 2조 5,109억 원으로 전망하는데, 두나무 인수로 인한 재무적 영향은 이후 반영 예정이다. " +
                        "네이버는 1분기 쇼핑 에이전트, 2분기 AI 탭, 이후 통합 에이전트를 출시할 계획으로, 국내 플랫폼 질서를 재정의하고 트래픽, 광고·커머스 매출을 흡수할 것으로 기대된다.")
                .source("인포스탁데일리")
                .url("https://www.infostockdaily.co.kr/news/articleView.html?idxno=213311")
                .publishedAt(lastWeek.plusHours(4))
                .relatedStockIds("5")
                .isAnalyzed(false)
                .build());

        // 4. 테슬라 - 모델 S/X 단종 발표
        newsList.add(News.builder()
                .title("테슬라, 모델 S·모델 X 2026년 2분기 단종 확정...로봇 '옵티머스' 생산 전환")
                .content("일론 머스크 테슬라 최고경영자(CEO)는 최근 실적 발표 컨퍼런스콜에서 2026년 2분기 말까지 모델 S와 모델 X의 생산을 완전히 중단할 계획이라고 공식적으로 밝혔다. " +
                        "2012년 첫선을 보인 모델 S와 2015년 팔콘 윙 도어로 시장에 충격을 안겼던 모델 X는 이로써 10년이 넘는 긴 여정을 마감하게 된다. " +
                        "머스크는 모델 S와 모델 X의 빈자리를 새로운 사업으로 채울 계획을 밝혔다. 기존 생산 라인은 테슬라가 미래 성장 동력으로 점찍은 휴머노이드 로봇 '옵티머스' 생산 라인으로 전환된다.")
                .source("News-WA")
                .url("https://www.news-wa.com/article/automobiles/2026/02/01/20260201500028")
                .publishedAt(lastWeek.plusHours(6))
                .relatedStockIds("2")
                .isAnalyzed(false)
                .build());

        // 5. 엔비디아 - CES 2026 발표
        newsList.add(News.builder()
                .title("엔비디아, CES 2026서 루빈 GPU 양산 가속화·자율주행 AI 공개")
                .content("젠슨 황 엔비디아 CEO는 CES 2026 기조연설에서 블랙웰을 이을 차세대 GPU 아키텍처 루빈이 예상보다 빠르게 본격 양산에 돌입했다고 밝혔다. 루빈은 블랙웰 대비 성능이 4배 향상됐으며, 추론 토큰 비용은 10분의 1로 절감됐다. " +
                        "엔비디아는 자율주행용 AI 모델인 'Cosmos'를 오픈소스로 공개했다. 생각하는 과정을 통해 주행의 논리적 근거를 설명할 수 있는 추론 기반 자율주행 AI로, 올 1분기 중 미국에서 실제 운행을 시작할 예정이다. " +
                        "메르세데스벤츠, 우버-루시드 등이 이를 채택하기로 했다.")
                .source("한국경제")
                .url("https://www.hankyung.com/article/202601075604i")
                .publishedAt(lastWeek.plusHours(8))
                .relatedStockIds("9")
                .isAnalyzed(false)
                .build());

        // 6. 코스피 시장 - 사상 최고치 경신
        newsList.add(News.builder()
                .title("코스피, 5,200 돌파하며 사상 최고치 경신...반도체 실적 견인")
                .content("벤치마크 KOSPI는 목요일 0.98% 상승하여 5,221로 마감하며 강력한 반도체 실적이 시장 심리를 끌어올리면서 사상 최고치를 기록했다. " +
                        "투자자들은 4분기 강력한 실적과 지속적인 AI 주도의 수요에 힘입어 첨단 메모리 제품의 지속적인 성장 기대를 강화하며 대형 반도체 제조업체에 몰렸다. " +
                        "주목할 만한 상승 종목으로는 삼성전자(+1.24%)와 SK하이닉스(+6.39%)가 포함되었다. 하락 종목으로는 현대차(-5.11%), 기아(-0.65%), LG에너지솔루션(-3.84%)이 있었다. " +
                        "그러나 시장 참가자들은 트럼프 미국 대통령이 한국에 대한 관세를 25%로 인상하겠다고 위협한 이전 뉴스 이후 선택적 이익 실현에 나섰다.")
                .source("Trading Economics")
                .url("https://ko.tradingeconomics.com/south-korea/stock-market")
                .publishedAt(lastWeek.plusHours(10))
                .relatedStockIds("")
                .isAnalyzed(false)
                .build());

        // 7. NVIDIA - Hopper(H100) 아키텍처 공개 (2023년 유사 사례)
        newsList.add(News.builder()
                .title("엔비디아, 차세대 Hopper H100 GPU 공개...AI 학습 성능 9배 향상")
                .content("엔비디아(NVIDIA)는 2023년 3월 GTC(GPU Technology Conference)에서 Hopper 아키텍처 기반의 H100 GPU를 공식 출시하며 AI 컴퓨팅 시장에 새로운 표준을 제시했다. " +
                        "H100은 이전 세대 A100 대비 AI 학습 속도가 최대 9배, 추론 성능은 최대 30배 향상된 것으로 나타났다. 특히 Transformer 엔진을 탑재해 대규모 언어 모델(LLM) 학습에 최적화된 성능을 제공한다. " +
                        "시장 분석가들은 H100 출시 이후 엔비디아의 데이터센터 사업 매출이 급증할 것으로 전망했다. 실제로 2023년 2분기부터 클라우드 서비스 업체들의 H100 주문이 폭증하면서 공급 부족 현상이 발생했으며, " +
                        "이는 엔비디아 주가를 연초 대비 240% 상승시키는 핵심 동력으로 작용했다. " +
                        "엔비디아 CEO 젠슨 황은 'AI의 아이폰 순간'이라고 표현하며 생성형 AI 시대의 본격적인 개막을 선언했다. " +
                        "업계 전문가들은 H100의 성공이 '성능 도약 + 생태계 독점' 패턴의 시작점이 됐으며, 이후 AI 인프라의 사실상 표준으로 자리잡게 됐다고 평가했다.")
                .source("AI Times")
                .url("https://www.aitimes.com/news/articleView.html?idxno=150234")
                .publishedAt(lastWeek.minusMonths(24))  // 약 2년 전
                .relatedStockIds("nvidia")
                .isAnalyzed(false)
                .build());

        // 8. NVIDIA - Blackwell 아키텍처 공개 (2024년 유사 사례)
        newsList.add(News.builder()
                .title("엔비디아 Blackwell GPU 공개, AI 추론 비용 '1/25'로 혁신...빅테크 CAPEX 확대 예고")
                .content("엔비디아는 2024년 3월 GTC 2024에서 차세대 Blackwell 아키텍처를 공개하며 AI 추론 시장의 게임체인저로 등장했다. " +
                        "Blackwell B100/B200 GPU는 2,080억 개의 트랜지스터를 탑재하고, Hopper 대비 AI 추론 성능이 최대 25배 향상되면서도 비용과 전력 소비는 크게 절감된 것으로 나타났다. " +
                        "특히 GPT-4 수준의 대규모 언어 모델 추론 비용을 기존 대비 1/25 수준으로 낮출 수 있다는 점이 주목받았다. " +
                        "이번 발표는 AI 산업의 패러다임 전환을 시사한다. 그동안 AI 시장은 '학습(Training)' 중심이었으나, Blackwell의 등장으로 '추론(Inference)' 중심으로 빠르게 전환될 것으로 예상된다. " +
                        "실제로 발표 직후 마이크로소프트, 구글, 아마zon, 메타 등 빅테크 기업들이 Blackwell 기반 데이터센터 투자 계획을 잇따라 발표했다. " +
                        "모건스탠리는 '2025년 클라우드 업체들의 AI 인프라 투자(CAPEX)가 전년 대비 40% 이상 증가할 것'이라고 전망하며, 그 중심에 Blackwell이 있다고 분석했다. " +
                        "시장은 이를 엔비디아의 지속적인 성장 동력으로 해석하며 주가는 발표 후 한 달간 35% 상승했다.")
                .source("TechCrunch Korea")
                .url("https://techcrunch.com/nvidia-blackwell-gpu-launch-2024")
                .publishedAt(lastWeek.minusMonths(10))  // 약 10개월 전
                .relatedStockIds("nvidia")
                .isAnalyzed(false)
                .build());

        // 9. NVIDIA - CUDA 생태계 확장 및 개발자 락인 전략 (반복 패턴)
        newsList.add(News.builder()
                .title("엔비디아 CUDA 생태계 확대...TensorRT·NeMo 오픈소스 공개로 개발자 락인 강화")
                .content("엔비디아가 AI 소프트웨어 생태계를 지속적으로 확장하며 '칩 + 소프트웨어 + 표준' 삼위일체 전략을 강화하고 있다. " +
                        "최근 엔비디아는 AI 추론 최적화 엔진 TensorRT-LLM, 대규모 언어 모델 개발 프레임워크 NeMo, 멀티모달 AI 툴킷 등을 오픈소스로 공개하며 개발자 커뮤니티를 적극 확대하고 있다. " +
                        "이는 2006년 출시된 CUDA(Compute Unified Device Architecture) 플랫폼의 연장선상에 있다. CUDA는 지난 18년간 전 세계 400만 명 이상의 개발자가 사용하며 GPU 컴퓨팅의 사실상 표준으로 자리잡았다. " +
                        "업계 전문가들은 엔비디아의 전략을 '기술적 해자(moat) 구축'이라고 평가한다. 하드웨어 성능만으로는 경쟁사가 따라잡을 수 있지만, " +
                        "수십만 줄의 CUDA 코드와 최적화된 라이브러리로 구성된 소프트웨어 생태계는 쉽게 대체할 수 없기 때문이다. " +
                        "실제로 AMD, 인텔 등 경쟁사들이 하드웨어 성능에서는 근접했음에도 불구하고 시장 점유율에서 엔비디아를 따라잡지 못하는 주요 이유로 CUDA 생태계가 꼽힌다. " +
                        "시티그룹 애널리스트는 '엔비디아의 진짜 경쟁력은 GPU가 아니라 CUDA'라며 '이는 장기적으로 80% 이상의 시장 점유율을 유지할 수 있는 핵심 요인'이라고 분석했다. " +
                        "최근에는 생성형 AI 붐과 함께 CUDA 기반 툴 수요가 폭발적으로 증가하면서 개발자 락인(lock-in) 효과가 더욱 강화되고 있다.")
                .source("ZDNet Korea")
                .url("https://zdnet.co.kr/view/?no=20240215093045")
                .publishedAt(lastWeek.minusMonths(6))  // 약 6개월 전
                .relatedStockIds("nvidia")
                .isAnalyzed(false)
                .build());

        List<News> savedNews = newsAiRepository.saveAll(newsList);
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
