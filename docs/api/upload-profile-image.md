# 프로필 이미지 업로드

domain: member

url: /api/members/{memberId}/profile-image

method: POST

# 01. 설명

회원의 프로필 이미지를 업로드(등록 또는 교체)할 때 사용한다. 이미지를 S3 에 저장하고 저장된 파일 정보를 반환한다.

- 필수 사항: `memberId`, `file`
- 선택 사항: 없음
- 이미 프로필 이미지가 있으면 새 이미지로 교체되고, 기존 이미지는 트랜잭션 커밋 이후 삭제 이벤트로 처리된다.
- 허용 형식은 JPEG, PNG, WebP 이며 크기는 10MB 이하여야 한다.
- `docs/api/README.md` 규칙(POST 는 201)과 다르게 생성(POST)이지만 `201 Created` 가 아닌 `200 OK` 를 반환한다. 코드 기준으로 작성함.
- 인증·권한 정책은 코드에서 확인되지 않음: 결정 필요

# 02. 요청(Request)

## a. Parameter & Querystring

```txt
/api/members/{memberId}/profile-image
/api/members/1/profile-image
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| memberId | Long | Y | 회원 식별자. 숫자가 아니면 `REQUEST_FORMAT_INVALID` |

## b. request headers

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| Content-Type | String | Y | `multipart/form-data` |

## c. request body

폼 필드 (`multipart/form-data`)

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| file | File | Y | 업로드할 이미지. 비어 있으면 안 된다. `image/jpeg`, `image/png`, `image/webp` 만 허용(요청의 Content-Type 기준). 10MB 이하. 서버 전체 업로드 한도 설정은 확인하지 못함: 결정 필요 |

# 03. 응답(Response)

## a. response header

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| 없음 | | | |

## b. response body

**성공 응답:** `200 OK`

```json
{
  "fileName": "profile.png",
  "fileKey": "profile-images/1/3f2b8c1e-0000-0000-0000-000000000000_profile.png"
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| fileName | String | Y | 업로드한 파일의 원본 이름 |
| fileKey | String | Y | S3 저장 키 (`profile-images/{memberId}/{UUID}_{원본파일명}`) |

**에러 응답:** `400 Bad Request` — 입력 검증에 실패했을 때

```json
{
  "status": 400,
  "code": "REQUEST_VALIDATION_FAILED",
  "message": "요청 데이터 검증에 실패했습니다.",
  "timestamp": "2026-11-17T10:00:00",
  "validationErrors": [
    { "field": "fileTypeValid", "message": "JPEG, PNG, WebP 형식의 이미지만 업로드할 수 있습니다." }
  ]
}
```

검증 실패 메시지 목록

| field | 조건 | message |
|---|---|---|
| file | `file` 파트 누락 | 업로드할 파일을 선택해주세요. |
| fileNotEmpty | 빈 파일 | 빈 파일은 업로드할 수 없습니다. |
| fileSizeValid | 10MB 초과 | 이미지 파일은 10MB 이하만 업로드할 수 있습니다. |
| fileTypeValid | JPEG, PNG, WebP 가 아님 | JPEG, PNG, WebP 형식의 이미지만 업로드할 수 있습니다. |

- `@AssertTrue` 검증은 getter 이름 기준 필드명(`fileNotEmpty` 등)으로 내려가는 것으로 코드상 추정되며 실제 응답 확인은 하지 못했다. 결정 필요(확인 필요)
- `validationErrors` 의 순서는 보장되지 않는다.

**에러 응답:** `400 Bad Request` — `memberId` 가 Long 으로 변환되지 않을 때

```json
{
  "status": 400,
  "code": "REQUEST_FORMAT_INVALID",
  "message": "잘못된 요청 형식입니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `400 Bad Request` — multipart 요청이 아니거나 형식이 잘못되었을 때

```json
{
  "status": 400,
  "code": "MULTIPART_REQUEST_INVALID",
  "message": "파일 업로드 요청 형식이 잘못되었습니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `404 Not Found` — 회원이 없을 때

```json
{
  "status": 404,
  "code": "MEMBER_NOT_FOUND",
  "message": "팀원을 찾을 수 없습니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `500 Internal Server Error` — S3 업로드에 실패했을 때

```json
{
  "status": 500,
  "code": "FILE_UPLOAD_FAILED",
  "message": "파일을 업로드할 수 없습니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| status | Integer | Y | HTTP 상태 코드 |
| code | String | Y | 에러 코드(`ErrorCode` 이름) |
| message | String | Y | 에러 메시지 |
| timestamp | String | Y | 에러 발생 일시 |
| validationErrors | Array | N | 검증 실패 목록. `REQUEST_VALIDATION_FAILED` 일 때만 포함 |
| validationErrors[].field | String | Y | 검증에 실패한 필드명 |
| validationErrors[].message | String | Y | 검증 실패 사유 |
