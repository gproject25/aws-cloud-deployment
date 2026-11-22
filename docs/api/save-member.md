# 회원 저장

domain: member

url: /api/members

method: POST

# 01. 설명

새 회원을 등록할 때 사용한다. 요청한 이름, 나이, MBTI 로 회원을 저장하고 저장된 회원 정보를 반환한다.

- 필수 사항: `name`, `age`, `mbti`
- 선택 사항: 없음
- 프로필 이미지는 이 API 로 등록하지 않는다. (별도 API)

# 02. 요청(Request)

## a. Parameter & Querystring

```txt
/api/members
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| 없음 | | | |

## b. request headers

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| Content-Type | String | Y | `application/json` (JSON 본문을 읽으므로 필요) |

## c. request body

```json
{
  "name": "gygim",
  "age": 25,
  "mbti": "INTJ"
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| name | String | Y | 회원 이름. 공백만 있거나 비어 있으면 안 된다. 최대 50자. 중복 허용 여부는 결정 필요 |
| age | Integer | Y | 나이. 1 이상 150 이하. 누락(null) 시 필수 입력 오류 |
| mbti | String | Y | MBTI 16유형 중 하나(`ISTJ`, `ISFJ`, `INFJ`, `INTJ`, `ISTP`, `ISFP`, `INFP`, `INTP`, `ESTP`, `ESFP`, `ENFP`, `ENTP`, `ESTJ`, `ESFJ`, `ENFJ`, `ENTJ`). 대문자만 허용 |

# 03. 응답(Response)

## a. response header

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| 없음 | | | |

## b. response body

**성공 응답:** `201 Created`

```json
{
  "id": 1,
  "name": "gygim",
  "age": 25,
  "mbti": "INTJ"
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| id | Long | Y | 저장된 회원 식별자 |
| name | String | Y | 회원 이름 |
| age | Integer | Y | 나이 |
| mbti | String | Y | MBTI 유형 |

**에러 응답:** `400 Bad Request` — 입력 검증에 실패했을 때

```json
{
  "status": 400,
  "code": "REQUEST_VALIDATION_FAILED",
  "message": "요청 데이터 검증에 실패했습니다.",
  "timestamp": "2026-11-17T10:00:00",
  "validationErrors": [
    { "field": "name", "message": "이름은 필수 입력 항목입니다." },
    { "field": "age", "message": "나이는 1살 이상이어야 합니다." },
    { "field": "mbti", "message": "올바른 MBTI 유형을 입력해주세요." }
  ]
}
```

검증 실패 메시지 목록 (필드별)

| field | 조건 | message |
|---|---|---|
| name | null, 빈 문자열, 공백만 있음 | 이름은 필수 입력 항목입니다. |
| name | 50자 초과 | 이름은 50자 이하여야 합니다. |
| age | null (누락 포함) | 나이는 필수 입력 항목입니다. |
| age | 1 미만 | 나이는 1살 이상이어야 합니다. |
| age | 150 초과 | 나이는 150살 이하여야 합니다. |
| mbti | null, 빈 문자열 | MBTI는 필수 입력 항목입니다. |
| mbti | 공백만 있음 | MBTI는 필수 입력 항목입니다. 와 올바른 MBTI 유형을 입력해주세요. (2건) |
| mbti | 16유형에 해당하지 않음 (소문자 포함) | 올바른 MBTI 유형을 입력해주세요. |

- 정규식이 빈 문자열을 허용하므로 빈 문자열 `mbti` 는 `@NotBlank` 에러 1건만 나온다. null 도 1건이다.
- 공백만 있는 `mbti`(예: `" "`)는 `@NotBlank` 와 `@Pattern` 이 모두 실패해 같은 field 에 에러 2건이 포함된다.
- `validationErrors` 의 순서는 보장되지 않는다.

**에러 응답:** `415 Unsupported Media Type` — `Content-Type` 이 `application/json` 이 아닐 때

```json
{
  "status": 415,
  "code": "UNSUPPORTED_MEDIA_TYPE",
  "message": "지원하지 않는 Content-Type입니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `400 Bad Request` — 요청 본문이 없거나 JSON 형식이 잘못되었을 때, 또는 필드 타입이 맞지 않을 때 (예: `age` 가 문자열)

```json
{
  "status": 400,
  "code": "REQUEST_FORMAT_INVALID",
  "message": "잘못된 요청 형식입니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `500 Internal Server Error` — 저장 중 예상하지 못한 오류가 발생했을 때

```json
{
  "status": 500,
  "code": "INTERNAL_SERVER_ERROR",
  "message": "서버 내부 오류가 발생했습니다.",
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
