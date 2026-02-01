# 🤖 AI 증권 뉴스 분석 시스템

OpenAI GPT를 활용한 24시간 증권 뉴스 자동 분석 시스템입니다.

## 📋 기능 소개

### 핵심 기능
1. **뉴스 AI 자동 분석**: 밤 사이 발생한 뉴스를 AI가 자동으로 분석
2. **맞춤형 영향 분석**: 사용자가 보유한 주식에 대한 영향을 개인화하여 분석
3. **유사 사례 탐색**: 과거 유사한 뉴스/사건을 찾아 당시 시장 반응과 결과 분석
4. **핵심 요약 제공**: 긴 뉴스 기사를 3-5문장으로 압축 요약

### 사용 시나리오
> "밤 10시에 잠들어서 아침 8시에 일어났을 때, 그 사이에 일어난 모든 뉴스를 AI가 분석해서 요약, 내 주식에 대한 영향, 과거 유사 사례까지 한 번에 보여줍니다."

## 🏗️ 아키텍처

### 데이터 흐름
```
뉴스 데이터 저장 (DB)
    ↓
API 호출 (/api/news-analysis/analyze-overnight?userId=1)
    ↓
NewsAnalysisService
    ↓
1. 해당 시간대 뉴스 조회
2. 사용자 보유 주식 정보 조회
3. OpenAI API 호출 (프롬프트 생성 → GPT 분석)
4. AI 응답 파싱 (요약, 영향 분석, 유사 사례)
5. 유사 과거 뉴스 검색
6. 분석 결과 DB 저장
    ↓
응답 반환 (NewsAnalysisResponse)
```

### 핵심 컴포넌트

#### 1. **엔티티 (Entity)**
- `News`: 뉴스 원본 데이터
- `NewsAnalysis`: AI 분석 결과
- `User`: 사용자 정보
- `UserStocks`: 사용자 보유 주식
- `Stocks`: 주식 기본 정보

#### 2. **서비스 (Service)**
- `NewsAnalysisService`: AI 분석 핵심 로직
  - `analyzeNewsForUser()`: 특정 시간대 뉴스 분석
  - `analyzeNewsWithAI()`: 단일 뉴스 AI 분석
  - `buildAnalysisPrompt()`: GPT 프롬프트 생성
  - `parseAIResponse()`: AI 응답 파싱
  - `findSimilarNews()`: 유사 뉴스 검색

#### 3. **컨트롤러 (Controller)**
- `NewsAnalysisController`: 뉴스 분석 API
- `NewsController`: 뉴스 CRUD API (테스트용)

## 🚀 사용 방법

### 1. 환경 설정

#### application-secret.yml 설정
```yaml
OPENAI_API_KEY: sk-your-api-key-here
DB_NAME: localdb
DB_USER: user
DB_PASSWORD: test123!
JWT_SECRET_KEY: your-jwt-secret-key
```

#### Docker로 MySQL 실행
```bash
docker-compose up -d
```

### 2. 애플리케이션 실행
```bash
export JAVA_HOME=~/jdk/jdk-21.0.1.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
./gradlew bootRun
```

### 3. API 테스트

#### 3.1 샘플 뉴스 생성 (테스트용)
```bash
curl -X POST http://localhost:8080/api/news/sample
```

응답:
```json
{
  "httpStatus": "OK",
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "삼성전자, 신규 반도체 공장 건설 발표",
      "content": "삼성전자가 경기도 평택에...",
      "publishedAt": "2026-02-01T12:00:00",
      "isAnalyzed": false
    }
  ]
}
```

#### 3.2 어젯밤 뉴스 자동 분석
```bash
curl -X GET "http://localhost:8080/api/news-analysis/analyze-overnight?userId=1"
```

응답:
```json
{
  "httpStatus": "OK",
  "success": true,
  "data": [
    {
      "analysisId": 1,
      "newsId": 1,
      "newsTitle": "삼성전자, 신규 반도체 공장 건설 발표",
      "summary": "삼성전자가 20조 원 규모의 3나노 공정 반도체 공장을 건설합니다...",
      "impactAnalysis": "보유 중인 삼성전자 주식에 긍정적 영향이 예상됩니다...",
      "similarCases": "2018년 삼성전자의 평택 2공장 건설 발표 당시...",
      "aiModel": "gpt-4",
      "confidenceScore": 0.85,
      "analyzedAt": "2026-02-01T22:30:00"
    }
  ]
}
```

#### 3.3 특정 기간 뉴스 분석
```bash
curl -X POST http://localhost:8080/api/news-analysis/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "startTime": "2026-02-01T22:00:00",
    "endTime": "2026-02-02T08:00:00"
  }'
```

### 4. Swagger UI로 테스트
브라우저에서 다음 주소로 접속:
```
http://localhost:8080/swagger-ui/index.html
```

## 🔍 AI 분석 프롬프트 구조

AI에게 전달되는 프롬프트는 다음과 같은 구조입니다:

```
다음 뉴스를 분석해주세요:

=== 뉴스 정보 ===
제목: [뉴스 제목]
내용: [뉴스 내용]
발행 시간: [발행 시간]

=== 투자자 보유 주식 ===
- 삼성전자 (005930): 반도체 섹터
- SK하이닉스 (000660): 반도체 섹터

다음 형식으로 분석 결과를 작성해주세요:

### 1. 핵심 요약
[3-5문장으로 뉴스의 핵심 내용을 요약]

### 2. 보유 주식에 대한 영향 분석
[투자자가 보유한 각 주식에 대해 이 뉴스가 미칠 영향을 분석]
[긍정적/부정적/중립적 영향을 명시하고 그 이유 설명]

### 3. 유사 과거 사례 분석
[과거에 유사한 뉴스나 사건이 있었는지 분석]
[유사 사례가 있다면 당시 시장 반응과 결과를 설명]
[투자 시사점 제시]
```

## 📊 데이터베이스 스키마

### news (뉴스)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGINT | PK |
| title | VARCHAR(500) | 뉴스 제목 |
| content | TEXT | 뉴스 내용 |
| source | VARCHAR(100) | 출처 |
| url | VARCHAR(1000) | URL |
| published_at | DATETIME | 발행 시간 |
| related_stock_ids | VARCHAR(500) | 관련 주식 ID (쉼표 구분) |
| is_analyzed | TINYINT(1) | 분석 완료 여부 |
| created_at | DATETIME | 생성 시간 |
| updated_at | DATETIME | 수정 시간 |

### news_analysis (뉴스 분석 결과)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGINT | PK |
| news_id | BIGINT | 뉴스 ID (FK) |
| user_id | BIGINT | 사용자 ID (FK) |
| summary | TEXT | AI 요약 내용 |
| impact_analysis | TEXT | 보유 주식 영향 분석 |
| similar_cases | TEXT | 과거 유사 사례 분석 |
| similar_news_ids | VARCHAR(500) | 유사 뉴스 ID들 |
| ai_model | VARCHAR(50) | AI 모델 버전 |
| confidence_score | DOUBLE | 신뢰도 점수 |
| created_at | DATETIME | 생성 시간 |
| updated_at | DATETIME | 수정 시간 |

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.4.3, Java 21
- **Database**: MySQL 8.0
- **AI**: OpenAI GPT-4 API
- **ORM**: Spring Data JPA
- **Documentation**: Swagger (SpringDoc OpenAPI)
- **Security**: Spring Security, JWT

## 📝 개발 과정 설명

### 1. 의존성 추가
`build.gradle`에 OpenAI API 라이브러리 추가:
```gradle
implementation 'com.theokanning.openai-gpt3-java:service:0.18.2'
```

### 2. API 키 설정
`application-secret.yml`에 OpenAI API 키 저장 (보안)

### 3. 엔티티 설계
- `News`: 뉴스 원본 저장
- `NewsAnalysis`: AI 분석 결과 저장 (사용자별로 다른 분석)

### 4. AI 분석 로직 구현
```java
// 1. 프롬프트 생성
String prompt = buildAnalysisPrompt(news, userStocksInfo);

// 2. OpenAI API 호출
ChatCompletionRequest request = ChatCompletionRequest.builder()
    .model("gpt-4")
    .messages(messages)
    .build();
String aiResponse = openAiService.createChatCompletion(request);

// 3. 응답 파싱
AnalysisResult result = parseAIResponse(aiResponse);

// 4. DB 저장
newsAnalysisRepository.save(analysis);
```

### 5. API 엔드포인트 제공
- `POST /api/news-analysis/analyze`: 특정 기간 분석
- `GET /api/news-analysis/analyze-overnight`: 어젯밤 자동 분석

## 🔐 보안 고려사항

1. **API 키 관리**: `application-secret.yml`은 `.gitignore`에 포함
2. **사용자 인증**: JWT 토큰 기반 인증 (추후 적용 가능)
3. **요청 제한**: OpenAI API 비용 고려하여 Rate Limiting 필요 (추후)

## 🚀 향후 개선 사항

1. **벡터 DB 연동**: 유사 뉴스 검색을 위한 임베딩 기반 검색
2. **실시간 뉴스 크롤링**: 뉴스 API 연동 (네이버, 다음, 구글 뉴스)
3. **배치 스케줄링**: Spring Batch로 매일 아침 자동 분석
4. **감정 분석**: 뉴스의 긍정/부정 감성 점수 추가
5. **알림 기능**: 중요 뉴스 발생 시 사용자에게 푸시 알림
6. **차트 연동**: 뉴스 발생 시점과 주가 변동 시각화

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
