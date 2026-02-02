# 🎙️ 네이버 클라우드 CLOVA Voice TTS 연동 완료

## 📋 구현 개요

네이버 클라우드 플랫폼의 **CLOVA Voice Premium API**를 사용하여 텍스트를 자연스러운 음성(MP3)으로 변환하는 기능을 구현했습니다.

---

## 🎯 주요 기능

1. **뉴스 분석 결과 → 음성 변환**: AI 분석된 뉴스를 자연스러운 음성으로 변환
2. **커스텀 텍스트 → 음성 변환**: 임의의 텍스트를 음성으로 변환
3. **다양한 화자 선택**: 14가지 화자 (한국어, 영어, 일본어, 중국어, 스페인어)
4. **음성 파라미터 조절**: 속도, 높낮이, 음량 조절 가능
5. **MP3 파일 생성 및 다운로드**: 서버에 저장 후 재생/다운로드

---

## 🏗️ 구현 과정 상세 설명

### 1단계: API 키 설정

#### 📁 `application-secret.yml`
```yaml
# 네이버 클라우드 CLOVA Voice API 키
NAVER_CLOVA_CLIENT_ID: vqmeez733z
NAVER_CLOVA_CLIENT_SECRET: qXwtob4ArNfYOnm2yVXEHWVqA4HkQNlJLJSfEh7q
```

**설명**: 
- 네이버 클라우드에서 발급받은 API 인증 정보를 환경변수로 설정
- `.gitignore`에 등록되어 있어 Git에 커밋되지 않음 (보안)

#### 📁 `application-local.yml`
```yaml
naver:
  clova:
    client-id: ${NAVER_CLOVA_CLIENT_ID}
    client-secret: ${NAVER_CLOVA_CLIENT_SECRET}
    api-url: https://naveropenapi.apigw-pub.fin-ntruss.com/tts-premium/v1/tts
```

**설명**:
- 환경변수를 Spring Boot 설정으로 매핑
- API URL은 네이버 클라우드 공식 문서에 명시된 주소 사용

---

### 2단계: RestTemplate 설정

#### 📁 `RestTemplateConfig.java`
```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 연결 타임아웃: 5초
        factory.setReadTimeout(30000);    // 읽기 타임아웃: 30초
        return new RestTemplate(factory);
    }
}
```

**설명**:
- `RestTemplate`: Spring에서 외부 HTTP API를 호출할 때 사용하는 클라이언트
- **연결 타임아웃 5초**: 네이버 서버에 연결을 시도할 최대 시간
- **읽기 타임아웃 30초**: TTS 생성은 시간이 걸리므로 충분한 시간 설정

---

### 3단계: CLOVA Voice 서비스 구현

#### 📁 `ClovaVoiceService.java`

핵심 로직을 단계별로 설명하겠습니다.

##### 3-1. API 호출 메서드
```java
public String textToSpeech(String text, String speaker, int speed, int pitch, int volume)
```

**동작 과정**:

1. **요청 헤더 설정**
```java
HttpHeaders headers = new HttpHeaders();
headers.set("x-ncp-apigw-api-key-id", clientId);           // Client ID
headers.set("x-ncp-apigw-api-key", clientSecret);          // Client Secret
headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
```
- 네이버 클라우드 API 인증을 위한 헤더 설정
- `x-ncp-apigw-api-key-id`: Client ID
- `x-ncp-apigw-api-key`: Client Secret
- `Content-Type`: `application/x-www-form-urlencoded` (폼 데이터 형식)

2. **요청 파라미터 설정**
```java
MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
params.add("speaker", speaker);    // 화자 (예: "jinho")
params.add("text", text);          // 변환할 텍스트
params.add("speed", String.valueOf(speed));   // 속도 (-5 ~ 5)
params.add("pitch", String.valueOf(pitch));   // 높낮이 (-5 ~ 5)
params.add("volume", String.valueOf(volume)); // 음량 (-5 ~ 5)
params.add("format", "mp3");       // 출력 형식: MP3
```
- CLOVA Voice API에서 요구하는 파라미터 구성
- 모든 값은 String 형태로 전달

3. **API 호출**
```java
ResponseEntity<byte[]> response = restTemplate.postForEntity(
    apiUrl,
    requestEntity,
    byte[].class
);
```
- POST 방식으로 API 호출
- 응답은 **바이너리 데이터(byte[])**로 수신 (MP3 파일 데이터)

4. **응답 처리**
```java
if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
    byte[] audioData = response.getBody();
    String filename = saveAudioFile(audioData);
    return filename;
}
```
- HTTP 200 OK면 MP3 바이너리 데이터를 파일로 저장
- 생성된 파일명 반환

##### 3-2. 파일 저장 메서드
```java
private String saveAudioFile(byte[] audioData) throws IOException {
    // 1. 디렉토리 생성
    Path audioDir = Paths.get("audio");
    if (!Files.exists(audioDir)) {
        Files.createDirectories(audioDir);
    }
    
    // 2. 파일명 생성 (UUID로 중복 방지)
    String filename = "clova_" + UUID.randomUUID().toString() + ".mp3";
    Path filepath = audioDir.resolve(filename);
    
    // 3. 파일 저장
    Files.write(filepath, audioData);
    
    return filename;
}
```

**설명**:
- `audio/` 디렉토리에 MP3 파일 저장
- 파일명은 `clova_[UUID].mp3` 형식 (예: `clova_550e8400-e29b-41d4-a716-446655440000.mp3`)
- UUID 사용으로 파일명 중복 방지

##### 3-3. 지원 화자(Speaker) 정의
```java
public enum Speaker {
    // 한국어
    NARA("nara", "한국어", "여성", "차분한 목소리"),
    JINHO("jinho", "한국어", "남성", "뉴스 앵커 스타일"),
    CLARA("clara", "한국어", "여성", "밝은 목소리"),
    // ... 14가지 화자
}
```
- 한국어, 영어, 일본어, 중국어, 스페인어 화자 지원
- Enum으로 타입 안전하게 관리

---

### 4단계: 뉴스 → 음성 변환 서비스

#### 📁 `NewsToSpeechService.java`

##### 4-1. 뉴스 분석 결과를 음성으로 변환
```java
public String convertAnalysisToSpeech(Long analysisId, String speaker)
```

**동작 과정**:

1. **DB에서 뉴스 분석 결과 조회**
```java
NewsAnalysis analysis = newsAnalysisResultRepository.findById(analysisId)
    .orElseThrow(() -> new RuntimeException("분석 결과를 찾을 수 없습니다"));
```

2. **음성 스크립트 생성**
```java
String script = buildSpeechScript(analysis);
```

예시 스크립트:
```
뉴스 분석 결과를 말씀드리겠습니다. 

먼저 뉴스 요약입니다. 삼성전자가 평택에 20조 원 규모의 신규 반도체 공장 건설을 발표했습니다...

핵심 내용입니다. 장기 성장 동력 확보, 글로벌 반도체 공급망 강화...

유사한 과거 사례입니다. 2018년 평택 2공장 건설 당시에도 주가가 상승했습니다...

이상 뉴스 분석을 마치겠습니다.
```

3. **CLOVA Voice로 TTS 변환**
```java
String audioFile = clovaVoiceService.textToSpeech(script, speaker);
```

##### 4-2. 텍스트 정제 (TTS 최적화)
```java
private String cleanTextForTts(String text) {
    return text
        .replaceAll("#{1,6}\\s*", "")              // ### 마크다운 제거
        .replaceAll("^[\\-\\*]\\s+", "")           // - bullet point 제거
        .replaceAll("\\n[\\-\\*]\\s+", ", ")       // 줄바꿈 bullet을 쉼표로
        .replaceAll("\\d+\\.\\s+", "")             // 1. 2. 숫자 리스트 제거
        .replaceAll("[\\*_]{1,2}([^\\*_]+)[\\*_]{1,2}", "$1")  // **볼드** 제거
        .replaceAll("\\(([^)]+)\\)", ", $1, ")     // (괄호)를 쉼표로
        .replaceAll("\\s+", " ")                   // 연속 공백 제거
        .trim();
}
```

**왜 필요한가?**
- AI 분석 결과는 마크다운 형식으로 작성됨 (예: `### 제목`, `- 항목`)
- TTS는 텍스트 그대로 읽기 때문에 "샵 샵 샵 제목" 같이 이상하게 읽힘
- 자연스러운 음성을 위해 마크다운 문법 제거

---

### 5단계: API 컨트롤러 구현

#### 📁 `ClovaVoiceTtsController.java`

##### 5-1. 뉴스 분석 음성 변환 API
```
POST /api/tts/news-analysis/{analysisId}?speaker=jinho
```

**요청 예시**:
```bash
curl -X POST "http://localhost:8080/api/tts/news-analysis/1?speaker=jinho"
```

**응답 예시**:
```json
{
  "success": true,
  "data": {
    "filename": "clova_550e8400-e29b-41d4-a716-446655440000.mp3",
    "downloadUrl": "/api/tts/audio/clova_550e8400-e29b-41d4-a716-446655440000.mp3",
    "speaker": "jinho",
    "message": "음성 변환이 완료되었습니다."
  }
}
```

##### 5-2. 커스텀 텍스트 음성 변환 API
```
POST /api/tts/custom
Content-Type: application/json
```

**요청 Body**:
```json
{
  "text": "안녕하세요. 오늘의 주요 뉴스를 전해드립니다.",
  "speaker": "nara",
  "speed": 0,
  "pitch": 0,
  "volume": 0
}
```

**파라미터 설명**:
- `text`: 변환할 텍스트 (필수)
- `speaker`: 화자 (선택, 기본값: "jinho")
- `speed`: 속도 -5~5 (선택, 기본값: 0)
- `pitch`: 높낮이 -5~5 (선택, 기본값: 0)
- `volume`: 음량 -5~5 (선택, 기본값: 0)

##### 5-3. 음성 파일 다운로드/재생 API
```
GET /api/tts/audio/{filename}
```

**사용 방법**:
1. 브라우저 주소창에 입력:
```
http://localhost:8080/api/tts/audio/clova_550e8400-xxx.mp3
```

2. HTML `<audio>` 태그로 재생:
```html
<audio controls>
  <source src="/api/tts/audio/clova_550e8400-xxx.mp3" type="audio/mpeg">
</audio>
```

##### 5-4. 화자 목록 조회 API
```
GET /api/tts/speakers
```

**응답**:
```json
{
  "success": true,
  "data": [
    {
      "code": "nara",
      "language": "한국어",
      "gender": "여성",
      "description": "차분한 목소리"
    },
    {
      "code": "jinho",
      "language": "한국어",
      "gender": "남성",
      "description": "뉴스 앵커 스타일"
    },
    // ... 14개 화자
  ]
}
```

---

### 6단계: Spring Security 설정

#### 📁 `Constants.java`
```java
public static final List<String> NO_NEED_AUTH_URLS = List.of(
    "/api/tts/**"  // TTS API는 인증 없이 접근 가능
);
```

**설명**:
- TTS API를 JWT 인증 없이 호출할 수 있도록 설정
- 테스트 및 프론트엔드 연동 편의성 향상

---

### 7단계: .gitignore 설정

#### 📁 `.gitignore`
```
/audio
```

**설명**:
- TTS로 생성된 MP3 파일은 Git에 커밋하지 않음
- 서버에서만 임시로 보관

---

## 📊 전체 아키텍처 흐름

```
[사용자 요청]
    ↓
[ClovaVoiceTtsController] (API 엔드포인트)
    ↓
[NewsToSpeechService] (비즈니스 로직)
    ├─ DB에서 뉴스 분석 조회
    ├─ 음성 스크립트 생성
    └─ ClovaVoiceService 호출
         ↓
[ClovaVoiceService] (CLOVA API 연동)
    ├─ RestTemplate으로 네이버 API 호출
    ├─ 요청 헤더 설정 (Client ID, Secret)
    ├─ 요청 파라미터 설정 (text, speaker 등)
    ├─ MP3 바이너리 데이터 수신
    └─ audio/ 디렉토리에 파일 저장
         ↓
[MP3 파일 반환]
    ↓
[사용자가 다운로드/재생]
```

---

## 🎨 화자(Speaker) 가이드

### 한국어 화자 (추천)

| 코드 | 성별 | 특징 | 추천 용도 |
|------|------|------|-----------|
| `jinho` | 남성 | 뉴스 앵커 스타일 | **뉴스 분석 (기본값)** ⭐ |
| `nara` | 여성 | 차분한 목소리 | 안내 멘트 |
| `clara` | 여성 | 밝은 목소리 | 친근한 안내 |
| `matt` | 남성 | 차분한 목소리 | 전문적인 설명 |
| `shinji` | 남성 | 은은한 목소리 | 부드러운 안내 |
| `dinna` | 여성 | 자연스러운 목소리 | 일반 안내 |

### 다국어 화자

- **영어**: `clara_en`, `matt_en`
- **일본어**: `yuri`, `shinji_jp`
- **중국어**: `meimei`, `liangliang`
- **스페인어**: `carmen`, `jose`

---

## 🚀 사용 방법

### Swagger에서 테스트

1. **Swagger UI 접속**
```
http://localhost:8080/swagger-ui/index.html
```

2. **화자 목록 확인**
```
GET /api/tts/speakers
→ 지원하는 14가지 화자 확인
```

3. **뉴스 분석 결과 → 음성 변환**
```
POST /api/tts/news-analysis/1?speaker=jinho
→ 뉴스 분석 ID 1번을 "jinho" 목소리로 변환
→ 응답에서 downloadUrl 확인
```

4. **음성 파일 재생**
```
GET /api/tts/audio/clova_xxx-xxx-xxx.mp3
→ 브라우저에서 바로 재생
```

5. **커스텀 텍스트 변환**
```
POST /api/tts/custom
Body:
{
  "text": "안녕하세요. 테스트입니다.",
  "speaker": "nara",
  "speed": 1,
  "pitch": 0,
  "volume": 0
}
→ 속도를 약간 빠르게 설정
```

---

## 💾 파일 구조

```
backend/
  ├─ audio/                          ← TTS MP3 파일 저장 (자동 생성)
  │   ├─ clova_xxx-xxx-xxx.mp3
  │   ├─ clova_yyy-yyy-yyy.mp3
  │   └─ ...
  ├─ src/
  │   ├─ main/
  │   │   ├─ java/.../
  │   │   │   ├─ config/
  │   │   │   │   └─ RestTemplateConfig.java      ← HTTP 클라이언트 설정
  │   │   │   ├─ service/
  │   │   │   │   ├─ ClovaVoiceService.java       ← CLOVA API 연동
  │   │   │   │   └─ NewsToSpeechService.java     ← 뉴스→음성 변환
  │   │   │   └─ controller/
  │   │   │       └─ ClovaVoiceTtsController.java ← TTS API
  │   │   └─ resources/
  │   │       ├─ application-secret.yml            ← API 키 설정
  │   │       └─ application-local.yml             ← CLOVA 설정
  └─ .gitignore                                   ← /audio 제외
```

---

## 🔧 핵심 기술 설명

### 1. RestTemplate (HTTP 클라이언트)
```java
RestTemplate restTemplate = new RestTemplate();
ResponseEntity<byte[]> response = restTemplate.postForEntity(url, request, byte[].class);
```
- Spring에서 제공하는 HTTP 클라이언트
- 외부 API 호출에 사용
- POST, GET, PUT, DELETE 등 모든 HTTP 메서드 지원

### 2. MultiValueMap (폼 데이터)
```java
MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
params.add("key", "value");
```
- HTML 폼처럼 `key=value&key2=value2` 형식으로 전송
- CLOVA Voice API는 `application/x-www-form-urlencoded` 형식 요구

### 3. byte[] (바이너리 데이터)
```java
ResponseEntity<byte[]> response = ...
byte[] audioData = response.getBody();
Files.write(path, audioData);
```
- MP3 파일은 바이너리 데이터
- `byte[]`로 받아서 그대로 파일로 저장

### 4. UUID (고유 파일명)
```java
String filename = "clova_" + UUID.randomUUID().toString() + ".mp3";
// 결과: clova_550e8400-e29b-41d4-a716-446655440000.mp3
```
- 파일명 중복 방지
- 128비트 랜덤 값으로 거의 중복 불가능

---

## ⚠️ 주의사항

### 1. API 키 보안
- `application-secret.yml`은 **절대 Git에 커밋하지 마세요**
- 이미 `.gitignore`에 등록되어 있음

### 2. 비용
- CLOVA Voice Premium은 **유료 서비스**
- 요금: 10,000자당 약 200원
- **테스트는 소량으로!**

### 3. 텍스트 길이 제한
- 1회 요청당 최대 **5,000자**
- 문장당 최대 **200자**
- 긴 텍스트는 나눠서 호출

### 4. 파일 용량 관리
- MP3 파일은 누적됨 (자동 삭제 안 됨)
- 주기적으로 `/audio` 디렉토리 정리 필요

---

## ✅ 구현 완료 체크리스트

- ✅ CLOVA Voice Premium API 연동
- ✅ RestTemplate 설정 및 HTTP 클라이언트 구현
- ✅ 뉴스 분석 결과 → 음성 변환
- ✅ 커스텀 텍스트 → 음성 변환
- ✅ 14가지 화자 지원 (한국어, 영어, 일본어, 중국어, 스페인어)
- ✅ 음성 파라미터 조절 (속도, 높낮이, 음량)
- ✅ MP3 파일 생성 및 저장
- ✅ 음성 파일 다운로드/재생 API
- ✅ 화자 목록 조회 API
- ✅ 마크다운 텍스트 정제 (TTS 최적화)
- ✅ Swagger API 문서화
- ✅ 에러 핸들링 및 로깅
- ✅ Spring Security 인증 제외 설정

---

## 🎉 완료!

이제 뉴스 분석 결과를 **jinho 앵커의 목소리**로 들을 수 있습니다!

**테스트 순서**:
1. Swagger UI 접속
2. 샘플 뉴스 생성 (`POST /api/news/sample`)
3. AI 분석 실행 (`POST /api/news-analysis/analyze-all`)
4. TTS 변환 (`POST /api/tts/news-analysis/1?speaker=jinho`)
5. 음성 재생 (`GET /api/tts/audio/clova_xxx.mp3`)

🚀 **시작하세요!**
