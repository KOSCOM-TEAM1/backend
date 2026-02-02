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
    @Operation(summary = "샘플 뉴스 생성", description = "테스트용 샘플 뉴스 데이터를 대량 생성합니다.")
    public ResponseEntity<ResponseDto<List<News>>> createSampleNews() {
        LocalDateTime lastNight = LocalDateTime.now().minusHours(10);
        
        List<News> newsList = new ArrayList<>();
        
        // 1. 삼성전자 반도체 공장 건설
        newsList.add(News.builder()
                .title("삼성전자, 평택에 20조원 규모 차세대 반도체 공장 건설")
                .content("삼성전자가 경기도 평택에 20조 원 규모의 차세대 반도체 공장 건설을 발표했다. "
                        + "이번 투자는 3나노 공정 기술을 적용한 최첨단 파운드리 생산 시설로, 2027년 완공을 목표로 한다. "
                        + "업계에서는 이번 투자가 글로벌 반도체 시장에서 삼성전자의 입지를 더욱 강화할 것으로 전망하고 있다. "
                        + "삼성전자는 이를 통해 TSMC와의 격차를 줄이고 AI 칩 시장 점유율을 확대할 계획이다.")
                .source("한국경제")
                .url("https://example.com/news/1")
                .publishedAt(lastNight)
                .relatedStockIds("1")
                .isAnalyzed(false)
                .build());

        // 2. 테슬라 가격 인하
        newsList.add(News.builder()
                .title("테슬라, 전 모델 가격 10% 대폭 인하... 전기차 시장 '가격 전쟁' 점화")
                .content("테슬라가 모든 전기차 모델의 가격을 평균 10% 인하한다고 발표했다. "
                        + "모델 3의 경우 5,000달러, 모델 Y는 7,000달러가 인하되며, 이는 경쟁 심화와 수요 둔화에 대응하기 위한 조치로 분석된다. "
                        + "전문가들은 이번 가격 인하가 국내 전기차 시장에도 영향을 미쳐 현대차, 기아 등 국내 업체들의 가격 전략 수정이 불가피할 것으로 보고 있다. "
                        + "중국의 BYD도 추가 가격 인하를 검토 중인 것으로 알려져 글로벌 전기차 시장의 가격 경쟁이 더욱 치열해질 전망이다.")
                .source("연합뉴스")
                .url("https://example.com/news/2")
                .publishedAt(lastNight.plusHours(1))
                .relatedStockIds("2,3")
                .isAnalyzed(false)
                .build());

        // 3. 미국 연준 금리 인상
        newsList.add(News.builder()
                .title("미 연준, 기준금리 0.25%p 추가 인상... '긴축 지속' 신호")
                .content("미국 연방준비제도(Fed)가 기준금리를 0.25%포인트 인상하며 5.50%로 상향 조정했다. "
                        + "제롬 파월 의장은 인플레이션 압력이 여전히 높다고 밝히며, 추가 금리 인상 가능성을 열어뒀다. "
                        + "시장에서는 이번 결정에 따라 외국인 자금 이탈 우려가 커지고 있으며, "
                        + "국내 증시는 단기적으로 하락 압력을 받을 것으로 예상된다. "
                        + "원·달러 환율도 상승세를 보이며 1,350원대를 넘어설 가능성이 제기되고 있다.")
                .source("매일경제")
                .url("https://example.com/news/3")
                .publishedAt(lastNight.plusHours(2))
                .relatedStockIds("")
                .isAnalyzed(false)
                .build());

        // 4. SK하이닉스 AI 메모리
        newsList.add(News.builder()
                .title("SK하이닉스, AI 전용 HBM3E 메모리 양산 돌입... 연내 매출 5조 목표")
                .content("SK하이닉스가 AI 전용 고대역폭 메모리 HBM3E의 양산에 돌입했다고 발표했다. "
                        + "HBM3E는 기존 제품 대비 50% 향상된 성능을 제공하며, 엔비디아의 차세대 AI 칩에 독점 공급될 예정이다. "
                        + "회사는 올해 HBM 관련 매출 5조 원을 목표로 하고 있으며, 이는 전년 대비 3배 이상 증가한 수치다. "
                        + "증권가에서는 AI 반도체 수요 급증에 힘입어 SK하이닉스의 실적이 크게 개선될 것으로 전망하고 있다.")
                .source("전자신문")
                .url("https://example.com/news/4")
                .publishedAt(lastNight.plusHours(3))
                .relatedStockIds("4")
                .isAnalyzed(false)
                .build());

        // 5. 네이버 AI 검색
        newsList.add(News.builder()
                .title("네이버, 초거대 AI '하이퍼클로바X' 기반 검색 서비스 전면 개편")
                .content("네이버가 자체 개발한 초거대 AI 하이퍼클로바X를 검색 서비스에 전면 적용한다고 밝혔다. "
                        + "새로운 AI 검색은 사용자 질문의 의도를 파악해 요약된 답변을 제공하며, 기존 키워드 중심 검색을 넘어선다. "
                        + "네이버는 이를 통해 구글, 마이크로소프트 등 글로벌 빅테크와의 AI 경쟁에서 우위를 점하겠다는 전략이다. "
                        + "업계에서는 AI 검색 시장이 향후 5년간 연평균 40% 이상 성장할 것으로 예상하고 있다.")
                .source("한국경제")
                .url("https://example.com/news/5")
                .publishedAt(lastNight.plusHours(4))
                .relatedStockIds("5")
                .isAnalyzed(false)
                .build());

        // 6. 현대차 전기차 수출
        newsList.add(News.builder()
                .title("현대차, 미국 전기차 시장 점유율 2위 도약... '아이오닉5' 돌풍")
                .content("현대자동차가 미국 전기차 시장에서 GM을 제치고 테슬라에 이어 2위를 차지했다. "
                        + "아이오닉5와 아이오닉6가 미국 소비자들에게 큰 호응을 얻으며 지난해 판매량이 전년 대비 150% 급증했다. "
                        + "미국 인플레이션 감축법(IRA) 혜택을 받을 수 있는 조지아 공장 완공도 긍정적으로 작용했다. "
                        + "현대차그룹은 2026년까지 전기차 17종을 출시하며 글로벌 전기차 시장 점유율 7% 달성을 목표로 하고 있다.")
                .source("조선일보")
                .url("https://example.com/news/6")
                .publishedAt(lastNight.plusHours(5))
                .relatedStockIds("6")
                .isAnalyzed(false)
                .build());

        // 7. LG에너지솔루션 배터리
        newsList.add(News.builder()
                .title("LG에너지솔루션, GM과 5조원 규모 배터리 공급 계약 체결")
                .content("LG에너지솔루션이 제너럴모터스(GM)와 향후 5년간 5조 원 규모의 전기차 배터리 공급 계약을 체결했다. "
                        + "이는 LG에너지솔루션 역사상 최대 규모의 단일 계약으로, GM의 전기차 전환 가속화에 핵심 파트너 역할을 하게 된다. "
                        + "양사는 미국과 캐나다에 합작 배터리 공장 2곳을 추가로 건설할 계획이며, "
                        + "이를 통해 연간 200GWh의 생산 능력을 확보한다. 증권가에서는 목표주가를 일제히 상향 조정했다.")
                .source("이데일리")
                .url("https://example.com/news/7")
                .publishedAt(lastNight.plusHours(6))
                .relatedStockIds("7")
                .isAnalyzed(false)
                .build());

        // 8. 카카오 AI 투자
        newsList.add(News.builder()
                .title("카카오, AI 스타트업에 1조원 투자... '생성AI 생태계' 구축")
                .content("카카오가 생성형 AI 관련 스타트업에 향후 3년간 1조 원을 투자한다고 발표했다. "
                        + "카카오는 자체 AI 모델 개발과 함께 유망 스타트업 육성을 통해 AI 생태계를 구축하겠다는 전략이다. "
                        + "특히 AI 음성 합성, 이미지 생성, 자연어 처리 등 다양한 분야의 기술을 확보해 "
                        + "카카오톡, 카카오페이 등 기존 서비스에 적용할 계획이다. "
                        + "업계에서는 빅테크의 AI 투자 경쟁이 본격화되고 있다고 분석했다.")
                .source("전자신문")
                .url("https://example.com/news/8")
                .publishedAt(lastNight.plusHours(7))
                .relatedStockIds("8")
                .isAnalyzed(false)
                .build());

        // 9. 엔비디아 신제품
        newsList.add(News.builder()
                .title("엔비디아, 차세대 AI 칩 'B200' 공개... 성능 5배 향상")
                .content("엔비디아가 차세대 AI 반도체 'B200'을 공개했다. B200은 기존 H100 대비 5배 향상된 성능을 제공하며, "
                        + "ChatGPT와 같은 초거대 AI 모델 학습 시간을 대폭 단축시킬 것으로 기대된다. "
                        + "젠슨 황 CEO는 AI 시대의 핵심 인프라가 될 것이라며 자신감을 표명했다. "
                        + "국내 반도체 업계에서는 삼성전자와 SK하이닉스가 B200용 메모리 공급을 놓고 경쟁을 벌일 것으로 전망된다. "
                        + "엔비디아 주가는 발표 직후 8% 급등하며 시가총액 3조 달러를 돌파했다.")
                .source("블룸버그통신")
                .url("https://example.com/news/9")
                .publishedAt(lastNight.plusHours(8))
                .relatedStockIds("1,4")
                .isAnalyzed(false)
                .build());

        // 10. 포스코 2차전지 소재
        newsList.add(News.builder()
                .title("포스코홀딩스, 양극재 생산능력 2배 확대... 4조원 투자")
                .content("포스코홀딩스가 전기차 배터리 핵심 소재인 양극재 생산능력을 2배로 늘리기 위해 4조 원을 투자한다. "
                        + "광양에 신규 공장을 건설하고 기존 설비를 증설해 2027년까지 연간 50만톤 생산 체제를 구축할 계획이다. "
                        + "이는 전기차 250만대 분량의 배터리를 만들 수 있는 규모다. "
                        + "포스코는 철강 중심에서 2차전지 소재 기업으로의 사업 전환을 가속화하고 있으며, "
                        + "2030년까지 2차전지 소재 매출 30조 원 달성을 목표로 하고 있다.")
                .source("머니투데이")
                .url("https://example.com/news/10")
                .publishedAt(lastNight.plusHours(9))
                .relatedStockIds("9")
                .isAnalyzed(false)
                .build());

        // 11. 삼성바이오로직스 CMO
        newsList.add(News.builder()
                .title("삼성바이오로직스, 美 화이자와 10조원 규모 위탁생산 계약")
                .content("삼성바이오로직스가 미국 제약사 화이자와 10조 원 규모의 바이오의약품 위탁생산(CMO) 계약을 체결했다. "
                        + "이는 국내 바이오 업계 역사상 최대 규모의 단일 계약으로, 향후 10년간 화이자의 주요 블록버스터 의약품을 생산하게 된다. "
                        + "삼성바이오로직스는 인천 송도에 5공장 건설을 확정하며 글로벌 1위 CMO 기업으로서의 입지를 더욱 공고히 했다. "
                        + "회사는 2030년까지 연매출 10조 원 달성을 목표로 하고 있다.")
                .source("한국경제")
                .url("https://example.com/news/11")
                .publishedAt(lastNight.plusMinutes(30))
                .relatedStockIds("10")
                .isAnalyzed(false)
                .build());

        // 12. 중국 경기 부양
        newsList.add(News.builder()
                .title("中 정부, 5조 위안 규모 경기 부양책 발표... 증시 '환호'")
                .content("중국 정부가 5조 위안(약 950조 원) 규모의 대규모 경기 부양책을 발표하며 침체된 경기 회복에 나섰다. "
                        + "부양책에는 부동산 규제 완화, 인프라 투자 확대, 소비 촉진을 위한 감세 조치 등이 포함됐다. "
                        + "발표 직후 상하이 종합지수는 5% 이상 급등했으며, 홍콩 항셍지수도 8% 상승했다. "
                        + "국내 증시에서도 중국 의존도가 높은 화학, 2차전지, 화장품 업종이 동반 상승하며 긍정적 영향을 받았다. "
                        + "전문가들은 중국 경기 회복이 글로벌 경제에 긍정적 신호가 될 것으로 분석했다.")
                .source("연합뉴스")
                .url("https://example.com/news/12")
                .publishedAt(lastNight.plusMinutes(90))
                .relatedStockIds("")
                .isAnalyzed(false)
                .build());

        // 13. 애플 비전프로
        newsList.add(News.builder()
                .title("애플 '비전 프로' 출시 임박... 삼성디스플레이 최대 수혜")
                .content("애플의 혼합현실(MR) 헤드셋 '비전 프로'가 다음 달 출시를 앞두고 있다. "
                        + "가격은 3,499달러로 책정됐으며, 초기 물량은 50만 대로 제한적이다. "
                        + "삼성디스플레이가 비전 프로의 핵심 부품인 마이크로 OLED 디스플레이를 독점 공급하며 최대 수혜 기업으로 떠올랐다. "
                        + "증권가에서는 비전 프로가 성공할 경우 MR 시장이 본격 개화하며 "
                        + "관련 부품 업체들의 실적이 크게 개선될 것으로 전망하고 있다.")
                .source("디지털타임스")
                .url("https://example.com/news/13")
                .publishedAt(lastNight.plusMinutes(150))
                .relatedStockIds("11")
                .isAnalyzed(false)
                .build());

        // 14. 두산에너빌리티 원전
        newsList.add(News.builder()
                .title("두산에너빌리티, 체코 원전 수주 유력... 28조원 규모")
                .content("두산에너빌리티가 체코 신규 원전 건설 프로젝트 우선협상대상자로 선정됐다. "
                        + "이번 프로젝트는 총 28조 원 규모로, 체코 두코바니 원전 2기를 건설하는 내용을 담고 있다. "
                        + "한국형 원전 APR1400이 채택되며, 두산에너빌리티는 원자로와 주요 기자재를 공급하게 된다. "
                        + "이는 UAE 바라카 원전 이후 15년 만의 해외 원전 수주로, "
                        + "정부의 원전 수출 재개 정책에 힘입어 폴란드, 영국 등 추가 수주도 기대되고 있다.")
                .source("서울경제")
                .url("https://example.com/news/14")
                .publishedAt(lastNight.plusMinutes(210))
                .relatedStockIds("12")
                .isAnalyzed(false)
                .build());

        // 15. KB금융 실적
        newsList.add(News.builder()
                .title("KB금융, 사상 최대 분기 순이익 2조원 돌파... 배당 확대")
                .content("KB금융지주가 4분기 순이익 2조 1,000억 원을 기록하며 분기 기준 사상 최대 실적을 달성했다. "
                        + "높은 금리 환경에서 이자 마진이 확대되고, 자산 건전성도 양호한 수준을 유지한 것이 주효했다. "
                        + "회사는 주주환원 정책 강화 차원에서 배당금을 전년 대비 20% 늘려 주당 2,500원을 지급하기로 결정했다. "
                        + "배당수익률은 4.5%로 업계 최고 수준이다. "
                        + "KB금융은 올해도 디지털 전환과 해외 사업 확대를 통해 성장세를 이어갈 계획이다.")
                .source("뉴스핌")
                .url("https://example.com/news/15")
                .publishedAt(lastNight.plusMinutes(270))
                .relatedStockIds("13")
                .isAnalyzed(false)
                .build());

        // 16. 한화에어로스페이스 방산
        newsList.add(News.builder()
                .title("한화에어로스페이스, 폴란드에 K9 자주포 600문 추가 수출")
                .content("한화에어로스페이스가 폴란드에 K9 자주포 600문을 추가 수출하는 계약을 체결했다. "
                        + "계약 규모는 약 8조 원으로, 앞서 체결한 180문 계약과 합쳐 총 780문을 수출하게 됐다. "
                        + "이는 단일 국가 대상 방산 수출 사상 최대 규모다. "
                        + "한화에어로스페이스는 폴란드 현지에 생산 공장도 설립해 일부 물량을 현지에서 생산할 계획이다. "
                        + "우크라이나 전쟁 이후 유럽의 방산 수요가 급증하며 K-방산의 위상이 크게 높아지고 있다.")
                .source("국민일보")
                .url("https://example.com/news/16")
                .publishedAt(lastNight.plusMinutes(330))
                .relatedStockIds("14")
                .isAnalyzed(false)
                .build());

        // 17. 셀트리온 바이오시밀러
        newsList.add(News.builder()
                .title("셀트리온, 美 FDA '스텔라라' 바이오시밀러 승인... 5조원 시장 공략")
                .content("셀트리온이 개발한 '스텔라라' 바이오시밀러가 미국 식품의약국(FDA) 승인을 획득했다. "
                        + "스텔라라는 건선, 크론병 등을 치료하는 블록버스터 의약품으로 연간 매출 5조 원 규모의 거대 시장이다. "
                        + "셀트리온은 오리지널 대비 40% 저렴한 가격으로 시장을 공략할 예정이며, "
                        + "3년 내 글로벌 시장 점유율 20% 달성을 목표로 하고 있다. "
                        + "이번 승인으로 셀트리온은 바이오시밀러 5대 제품군을 모두 확보하며 글로벌 톱3 제약사로 도약할 발판을 마련했다.")
                .source("팜뉴스")
                .url("https://example.com/news/17")
                .publishedAt(lastNight.plusMinutes(390))
                .relatedStockIds("15")
                .isAnalyzed(false)
                .build());

        // 18. 코스피 급등
        newsList.add(News.builder()
                .title("코스피, 3,200선 돌파... 외국인·기관 동반 매수세")
                .content("코스피 지수가 6개월 만에 3,200선을 돌파하며 강세를 보였다. "
                        + "전날 종가 대비 85포인트(2.7%) 오른 3,205.42에 거래를 마쳤다. "
                        + "외국인과 기관이 각각 1조 2,000억 원, 8,000억 원 규모로 동반 매수에 나서며 지수 상승을 견인했다. "
                        + "반도체, 2차전지, 조선 등 수출 주도주가 강세를 보였으며, "
                        + "특히 삼성전자는 8만원을 회복하며 시가총액 500조 원을 넘어섰다. "
                        + "증권가에서는 실적 개선 기대감과 밸류에이션 매력이 부각되며 추가 상승 여력이 있다고 분석했다.")
                .source("서울경제")
                .url("https://example.com/news/18")
                .publishedAt(lastNight.plusMinutes(450))
                .relatedStockIds("")
                .isAnalyzed(false)
                .build());

        // 19. 카카오뱅크 성장
        newsList.add(News.builder()
                .title("카카오뱅크, 예금 100조원 돌파... 출범 6년만에 '제2금융권 1위'")
                .content("카카오뱅크의 예금 잔액이 100조 원을 돌파하며 제2금융권 1위에 올랐다. "
                        + "2017년 출범 이후 6년 만의 쾌거로, 편리한 모바일 뱅킹과 높은 예금금리가 주효했다. "
                        + "고객 수는 2,100만 명을 넘어 대한민국 성인 3명 중 1명이 카카오뱅크를 이용하고 있다. "
                        + "카카오뱅크는 올해 중소기업 대출과 신용카드 사업을 본격화하며 수익 다각화에 나선다. "
                        + "목표는 2027년까지 순이익 1조 원 달성이다.")
                .source("파이낸셜뉴스")
                .url("https://example.com/news/19")
                .publishedAt(lastNight.plusMinutes(510))
                .relatedStockIds("16")
                .isAnalyzed(false)
                .build());

        // 20. 원달러 환율
        newsList.add(News.builder()
                .title("원·달러 환율 1,380원 돌파... 수출기업 '웃고' 수입기업 '울고'")
                .content("원·달러 환율이 1,380원을 돌파하며 6개월 만에 최고치를 기록했다. "
                        + "미국의 고금리 기조 장기화 전망과 중동 지정학적 리스크 고조가 원인으로 분석된다. "
                        + "환율 상승은 삼성전자, 현대차 등 수출 대기업에는 긍정적이지만, "
                        + "원자재 수입 비중이 높은 항공, 정유, 화학 업종에는 부담으로 작용한다. "
                        + "한국은행은 외환시장 안정을 위해 개입 가능성을 시사했으며, "
                        + "증권가에서는 당분간 환율 변동성이 확대될 것으로 전망하고 있다.")
                .source("이투데이")
                .url("https://example.com/news/20")
                .publishedAt(lastNight.plusMinutes(570))
                .relatedStockIds("")
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
