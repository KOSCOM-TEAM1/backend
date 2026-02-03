# Koscom Hackathon API

증권 뉴스를 AI로 분석하고, 분석 결과를 음성(TTS)으로 읽어주는 백엔드 API 서비스입니다.

---

## 📋 목차

- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [요구 사항](#-요구-사항)
- [프로젝트 구조](#-프로젝트-구조)
- [설정 및 실행](#-설정-및-실행)
- [API 개요](#-api-개요)
- [관련 문서](#-관련-문서)

---

## 🎯 주요 기능

| 기능 | 설명 |
|------|------|
| **뉴스 관리** | 뉴스 CRUD, 샘플 데이터 생성, 미분석 뉴스 조회 |
| **AI 뉴스 분석** | OpenAI(GPT)로 뉴스 요약, 핵심 내용, 유사 과거 사례 분석 |
| **TTS (음성 합성)** | 네이버 CLOVA Voice로 분석 결과를 아나운서 목소리(MP3)로 변환 |
| **인증** | JWT 기반 인증 (Spring Security) |
| **API 문서** | Swagger UI 제공 |

---

## 🛠 기술 스택

| 구분 | 기술 |
|------|------|
| **언어** | Java 17 |
| **프레임워크** | Spring Boot 3.4.x |
| **빌드** | Gradle 8.x |
| **DB** | MySQL 8.0 (JPA/Hibernate) |
| **인증** | JWT, Spring Security |
| **AI** | OpenAI API (GPT) |
| **TTS** | 네이버 클라우드 CLOVA Voice Premium |
| **문서** | SpringDoc OpenAPI (Swagger UI) |

---

## 📌 요구 사항

- **JDK 17** 이상
- **Docker** (로컬 DB용)
- **MySQL 8.0** (Docker 또는 로컬 설치)
- **OpenAI API 키** (뉴스 AI 분석용)
- **네이버 클라우드 CLOVA Voice API 키** (TTS용)

---

## 📁 프로젝트 구조

```
backend/
├── src/main/java/com/hyeongkyu/template/
│   ├── controller/          # API 컨트롤러
│   │   ├── ClovaVoiceTtsController.java   # TTS API
│   │   ├── NewsAnalysisController.java   # AI 뉴스 분석 API
│   │   ├── NewsController.java           # 뉴스 CRUD
│   │   ├── StockController.java
│   │   ├── UserController.java
│   │   └── HealthCheckController.java
│   ├── service/             # 비즈니스 로직
│   │   ├── ClovaVoiceService.java        # CLOVA Voice 연동
│   │   ├── NewsToSpeechService.java      # 뉴스 → 음성 변환
│   │   ├── NewsAnalysisService.java      # OpenAI 뉴스 분석
│   │   ├── StockService.java
│   │   └── UserService.java
│   ├── domain/               # 엔티티, DTO
│   ├── repository/           # JPA 리포지토리
│   ├── global/               # 공통 설정, 예외 처리
│   └── security/             # Spring Security 설정
├── src/main/resources/
│   ├── application.yml
│   ├── application-local.yml
│   ├── application-prod.yml
│   └── application-secret.yml   # ⚠️ Git 제외, 직접 생성
├── docker-compose.yml        # MySQL 로컬 실행
├── build.gradle
└── README.md
```

---

## ⚙️ 설정 및 실행

### 1. 시크릿 설정 파일 생성

`src/main/resources/application-secret.yml` 파일을 생성하고 아래 값을 채웁니다.

```yaml
# DB (docker-compose.yml과 동일하게)
DB_NAME: localdb
DB_USER: user
DB_PASSWORD: test123!

# JWT (256비트 이상 권장)
JWT_SECRET_KEY: <본인 JWT 시크릿 키>

# OpenAI API (뉴스 AI 분석용)
OPENAI_API_KEY: <OpenAI API 키>

# 네이버 CLOVA Voice (TTS용)
NAVER_CLOVA_CLIENT_ID: <네이버 클라우드 Client ID>
NAVER_CLOVA_CLIENT_SECRET: <네이버 클라우드 Client Secret>
```

> ⚠️ `application-secret.yml`은 `.gitignore`에 포함되어 있으므로 Git에 커밋되지 않습니다.  
> 팀원은 위 템플릿을 참고해 각자 로컬에 파일을 만들어 사용하세요.

### 2. MySQL 실행 (Docker)

```bash
docker-compose up -d
```

- **포트**: 호스트 `3308` → 컨테이너 `3306`
- **DB 이름**: `localdb`
- **사용자**: `user` / **비밀번호**: `test123!`

### 3. 애플리케이션 실행

```bash
# JDK 17 필요
./gradlew bootRun
```

기본 포트: **8080**  
프로파일: **local** (application-local.yml + application-secret.yml 사용)

### 4. Swagger UI 접속

브라우저에서 다음 주소로 접속합니다.

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📡 API 개요

### 뉴스 (`/api/news`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/news` | 뉴스 1건 생성 |
| GET | `/api/news` | 전체 뉴스 조회 |
| GET | `/api/news/unanalyzed` | 미분석 뉴스만 조회 |
| GET | `/api/news/{id}` | 뉴스 1건 조회 |
| POST | `/api/news/sample` | 테스트용 샘플 뉴스 9건 생성 |

### AI 뉴스 분석 (`/api/news-analysis`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/news-analysis/analyze-all` | 미분석 뉴스 전체 AI 분석 |
| POST | `/api/news-analysis/analyze/{newsId}` | 특정 뉴스 1건 AI 분석 |

응답: 요약, 핵심 내용, 유사 과거 사례 등 (OpenAI GPT 활용).

### TTS 음성 합성 (`/api/tts`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/tts/news-analysis/{analysisId}` | 분석 결과를 음성(MP3)으로 변환 (기본: 아나운서 음성 `jinho`) |
| POST | `/api/tts/custom` | 임의 텍스트를 음성으로 변환 |
| GET | `/api/tts/audio/{filename}` | 생성된 MP3 파일 재생/다운로드 |
| GET | `/api/tts/speakers` | 지원 화자 목록 조회 |

### 기타

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/v1/health/**` | 헬스 체크 |

---

## 🧪 빠른 테스트 흐름

1. **DB + 앱 실행**  
   `docker-compose up -d` → `./gradlew bootRun`

2. **샘플 뉴스 생성**  
   Swagger에서 `POST /api/news/sample` 호출 → 9건 생성

3. **AI 분석**  
   `POST /api/news-analysis/analyze-all` → 미분석 뉴스 전체 분석

4. **TTS 변환**  
   `POST /api/tts/news-analysis/1?speaker=jinho` (분석 ID는 응답 기준으로 변경)  
   → 응답의 `downloadUrl`로 MP3 재생

5. **음성 재생**  
   `GET /api/tts/audio/clova_xxx.mp3` 또는 브라우저에 URL 입력

---

## 📚 관련 문서

| 문서 | 설명 |
|------|------|
| [CLOVA_VOICE_TTS_GUIDE.md](./CLOVA_VOICE_TTS_GUIDE.md) | CLOVA Voice TTS 연동 상세 가이드 |
| [AI_NEWS_ANALYSIS_README.md](./AI_NEWS_ANALYSIS_README.md) | AI 뉴스 분석 기능 설명 |
| [SIMPLE_API_GUIDE.md](./SIMPLE_API_GUIDE.md) | API 사용 간단 가이드 |

---

## 📄 라이선스 및 기타

- **Spring Boot Starter Kit** 기반
- Koscom Hackathon 프로젝트용 백엔드

문의나 이슈는 팀 내부 채널을 이용해 주세요.
