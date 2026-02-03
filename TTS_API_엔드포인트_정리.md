# 🎙️ TTS 사용 API 및 엔드포인트 정리

## 1. 우리 백엔드 (Spring Boot) – 프론트/클라이언트가 호출하는 API

| 메서드 | 엔드포인트 | 설명 |
|--------|------------|------|
| POST | `/api/tts/custom` | **커스텀 텍스트 → 음성** (Body: `text`, `speaker`, `speed`, `pitch`, `volume`) |
| POST | `/api/tts/news-analysis/{analysisId}` | **뉴스 분석 결과 → 음성** (Query: `?speaker=nara`) |
| GET | `/api/tts/audio/{filename}` | 생성된 MP3 **재생/다운로드** |
| GET | `/api/tts/speakers` | 지원 **화자 목록** 조회 |

- **Base URL**: 서버 주소 (예: `http://221.168.36.171:8080` 또는 배포 URL)
- **설정**: `application-local.yml` / `application-prod.yml` 의 `spring.datasource` 등으로 서버가 기동되는 주소가 결정됨

---

## 2. 외부 API (네이버 클라우드) – 백엔드가 내부적으로 호출하는 API

우리 백엔드는 **네이버 클라우드 CLOVA Voice TTS Premium** 을 사용합니다.

| 항목 | 값 |
|------|-----|
| **엔드포인트** | `https://naveropenapi.apigw-pub.fin-ntruss.com/tts-premium/v1/tts` |
| **메서드** | POST |
| **Content-Type** | `application/x-www-form-urlencoded` |
| **헤더** | `x-ncp-apigw-api-key-id` (Client ID), `x-ncp-apigw-api-key` (Client Secret) |
| **Body 파라미터** | `speaker`, `text`, `speed`, `pitch`, `volume`, `format`(mp3) |

- **설정 위치**
  - **local**: `application-local.yml` → `naver.clova.api-url`
  - **prod**: `application-prod.yml` → `naver.clova.api-url`
- **환경 변수**: `NAVER_CLOVA_CLIENT_ID`, `NAVER_CLOVA_CLIENT_SECRET` (실제 값은 시크릿/환경변수로 설정)

즉, **지금 쓰는 API는 위 fin-ntruss TTS Premium 한 개**입니다.

---

## 3. fin-ntruss TTS Premium 지원 화자 (참고)

일부 문서/실제 동작 기준으로, **fin-ntruss** 엔드포인트에서 사용 가능한 화자 예시는 다음과 같습니다.

- **여성**: `nara`, `nara_call`, `nbora`, `ndain`, `nes_c_hyeri`, `nes_c_mikyung`, `nes_c_sohyun`, `neunseo`, `neunyoung`, `ngoeun`, `nheera`, `nihyun`, `njiwon`, `njiyun`, `napple`, `mijin` 등
- **남성**: `jinho`, `njinho`, `ndaeseong`, `ndonghyun`, `nes_c_kihyo`, `nian`, `njaewook`, `njihun`, `njihwan` 등

**기본값/안전값**: `nara` (우리 백엔드 기본값)

- 화자 코드는 **정확한 문자열**이어야 합니다. 오타나 `"string"` 같은 잘못된 값이 들어가면 `Unsupported speaker (지원하지 않는 화자입니다.)` / `invalid speaker: string` 오류가 납니다.

---

## 4. 오류가 계속될 때 확인할 것

### 4-1. `invalid speaker: string` / `Unsupported speaker` (네이버 API 400)

- **의미**: 네이버 API로 전달된 `speaker` 값이 비어있거나, `"string"` 리터럴이거나, 지원하지 않는 코드임.
- **우리 백엔드 조치**
  - `ClovaVoiceService.resolveSpeaker()` 에서
    - `null`/빈값/`"string"`(대소문자 무관)/미지원 코드 → **`nara`** 로 치환하도록 되어 있음.
  - 따라서 **최신 코드가 배포되어 있다면** `speaker`가 잘못 들어와도 서버에서는 `nara`로 보정해 호출함.
- **확인할 것**
  1. 백엔드 **재빌드 및 재배포** 후 다시 호출해 보기.
  2. 로그에서 `CLOVA Voice TTS 요청 - 텍스트 길이: ..., 화자: nara (요청: ???)` 형태로 **실제 요청값(???)** 이 무엇으로 찍히는지 확인.
  3. 프론트/클라이언트에서 `speaker` 를 **절대 `"string"` 이나 타입 이름으로 보내지 않도록** 수정 (기본값 `nara` 사용 권장).

### 4-2. 프론트엔드 Network Error / 404

- **우리 백엔드**가 아니라 **브라우저 → 우리 백엔드** 구간 문제일 수 있음.
- 확인할 것:
  1. **Base URL**: 프론트에서 TTS를 호출할 때 사용하는 **API Base URL** 이 실제 백엔드 주소인지 (Vercel 등 배포 환경에서는 `VITE_API_BASE_URL` 등 환경 변수 설정).
  2. **CORS**: 백엔드에서 프론트 도메인(예: `https://track24.vercel.app`)이 허용되어 있는지.
  3. **Mixed Content**: HTTPS 페이지에서 HTTP API 호출 시 브라우저가 막을 수 있음 → 가능하면 백엔드도 HTTPS 사용.

---

## 5. 한 줄 요약

- **우리가 쓰는 API**: 네이버 클라우드 **CLOVA Voice TTS Premium**  
  **엔드포인트**: `https://naveropenapi.apigw-pub.fin-ntruss.com/tts-premium/v1/tts`
- **우리 백엔드 TTS**: `POST /api/tts/custom`, `POST /api/tts/news-analysis/{id}`, `GET /api/tts/audio/{filename}`, `GET /api/tts/speakers`
- **오류 시**: 백엔드 재배포 여부, 로그의 `화자: ??? (요청: ???)` 확인, 프론트의 `speaker`/Base URL/CORS 확인.
