# 🤖 OpenAI 뉴스 분석 API - 사용 가이드

## 📌 개요

DB에 저장된 증권 뉴스들을 OpenAI GPT에게 던져서 자동으로 분석하는 API입니다.

### AI가 제공하는 것
1. **뉴스 요약**: 긴 뉴스를 3-4문장으로 압축
2. **핵심 내용**: 투자자가 꼭 알아야 할 포인트 3-5개
3. **유사 과거 사례**: 과거에 비슷한 뉴스가 있었는지 + 당시 시장 반응

## 🚀 빠른 시작

### 1. 애플리케이션 실행

```bash
# Docker로 MySQL 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun
```

### 2. Swagger UI 접속

브라우저에서 다음 주소로 접속:
```
http://localhost:8080/swagger-ui/index.html
```

### 3. 테스트 순서

#### Step 1: 샘플 뉴스 생성
- API: `POST /api/news/sample`
- 설명: 테스트용 샘플 뉴스 3개 자동 생성
- Try it out 클릭 → Execute

#### Step 2: 뉴스 확인
- API: `GET /api/news/unanalyzed`
- 설명: 미분석 뉴스 목록 확인
- Try it out 클릭 → Execute

#### Step 3: AI 분석 실행
- API: `POST /api/news-analysis/analyze-all`
- 설명: 미분석 뉴스들을 OpenAI로 분석
- Try it out 클릭 → Execute
- ⚠️ OpenAI API 호출되므로 10-30초 소요될 수 있음

## 📋 API 목록

### 뉴스 관리 API

#### 1. 샘플 뉴스 생성
```
POST /api/news/sample
```
테스트용 샘플 뉴스 3개 자동 생성

#### 2. 전체 뉴스 조회
```
GET /api/news
```
DB에 저장된 모든 뉴스 조회

#### 3. 미분석 뉴스 조회
```
GET /api/news/unanalyzed
```
아직 AI 분석이 안 된 뉴스만 조회

#### 4. 뉴스 생성
```
POST /api/news
```
새로운 뉴스 직접 생성

**요청 예시:**
```json
{
  "title": "애플, 새로운 AI 칩 발표",
  "content": "애플이 자체 개발한 AI 전용 칩을 공개했다...",
  "source": "TechCrunch",
  "url": "https://example.com/news",
  "publishedAt": "2026-02-01T10:00:00",
  "relatedStockIds": "1,2"
}
```

### AI 분석 API

#### 1. 전체 미분석 뉴스 분석
```
POST /api/news-analysis/analyze-all
```
DB에 있는 모든 미분석 뉴스를 OpenAI로 분석

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "analysisId": 1,
      "newsId": 1,
      "newsTitle": "삼성전자, 신규 반도체 공장 건설 발표",
      "newsSummary": "삼성전자가 경기도 평택에 20조 원 규모의...",
      "summary": "삼성전자가 평택에 3나노 공정 반도체 공장 건설을 발표했습니다. 2027년 완공 목표로 20조 원이 투자되며...",
      "impactAnalysis": "- 삼성전자의 장기 성장 동력 확보 신호\n- 글로벌 반도체 시장 점유율 상승 기대\n- 단기적으로는 대규모 투자로 현금 흐름 감소 우려",
      "similarCases": "2018년 평택 2공장 건설 발표 당시, 발표 직후 단기 조정(-5%)이 있었으나 3개월 후 15% 상승...",
      "aiModel": "gpt-4",
      "confidenceScore": 0.85
    }
  ]
}
```

#### 2. 특정 뉴스만 분석
```
POST /api/news-analysis/analyze/{newsId}
```
특정 뉴스 ID를 지정하여 분석

**예시:**
```
POST /api/news-analysis/analyze/1
```

## 🔍 AI 프롬프트 구조

OpenAI에게 전달되는 프롬프트:

```
다음 증권 뉴스를 분석해주세요:

=== 뉴스 정보 ===
제목: [뉴스 제목]
내용: [뉴스 내용]
출처: [출처]
발행 시간: [시간]

다음 형식으로 분석 결과를 작성해주세요:

### 1. 뉴스 요약
[3-4문장으로 명확하게 요약]

### 2. 핵심 내용
- [핵심 포인트 1]
- [핵심 포인트 2]
- [핵심 포인트 3]

### 3. 유사 과거 사례
[과거 유사 사례 + 당시 시장 반응 + 결과]
```

## 💡 실제 사용 시나리오

### 시나리오: 밤 사이 뉴스 자동 분석

```
1. 외부 뉴스 API (네이버, 다음 등)에서 뉴스 크롤링
   └─> News 테이블에 저장 (is_analyzed = false)
   
2. 아침 8시에 배치 작업 실행
   └─> POST /api/news-analysis/analyze-all 호출
   
3. AI가 모든 미분석 뉴스 분석
   └─> NewsAnalysis 테이블에 결과 저장
   
4. 사용자가 앱 열면 분석된 뉴스 요약 확인
   └─> 긴 기사 안 읽어도 핵심만 파악 가능
```

## 📊 데이터베이스 구조

### news 테이블
- 뉴스 원본 데이터 저장
- `is_analyzed`: false → OpenAI 분석 대기 / true → 분석 완료

### news_analysis 테이블
- AI 분석 결과 저장
- `summary`: 요약
- `impact_analysis`: 핵심 내용 (컬럼명은 impact_analysis지만 실제로는 핵심 포인트 저장)
- `similar_cases`: 유사 과거 사례

## 🔐 환경 설정

### application-secret.yml
```yaml
OPENAI_API_KEY: sk-your-api-key-here
DB_NAME: localdb
DB_USER: user
DB_PASSWORD: test123!
```

## ⚠️ 주의사항

1. **OpenAI API 비용**: GPT-4는 비용이 발생합니다. 테스트는 소량으로!
2. **응답 시간**: 뉴스가 많으면 시간이 오래 걸립니다 (뉴스 1개당 약 5-10초)
3. **API 키 보안**: `application-secret.yml`은 Git에 올리지 마세요!

## 🚀 향후 개선 아이디어

1. **배치 처리**: Spring Batch로 매일 자동 실행
2. **비동기 처리**: 많은 뉴스를 빠르게 처리 (CompletableFuture)
3. **감정 분석**: 뉴스의 긍정/부정 감성 점수 추가
4. **알림 기능**: 중요 뉴스 발생 시 푸시 알림
5. **캐싱**: 같은 뉴스 재분석 방지

## 📞 트러블슈팅

### 문제: OpenAI API 호출 실패
```
해결: application-secret.yml의 API 키 확인
```

### 문제: DB 연결 실패
```
해결: docker-compose up -d 실행했는지 확인
```

### 문제: 빌드 실패
```
해결: Java 21이 설치되어 있는지 확인
./gradlew -q javaToolchains
```

## 🎯 핵심 코드 위치

- **AI 분석 로직**: `NewsAnalysisService.java`
- **API 엔드포인트**: `NewsAnalysisController.java`
- **엔티티**: `News.java`, `NewsAnalysis.java`
- **설정**: `application-secret.yml`, `OpenAiConfig.java`
