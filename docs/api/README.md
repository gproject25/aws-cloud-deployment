# API 명세 작성 지침서

## 언제 수정하나

- API 가 바뀌면 코드보다 명세를 먼저 고친다.
- API 하나당 파일 하나: `docs/api/<기능명>.md` (예: `docs/api/signup.md`, `docs/api/update-member.md`)
- 같은 API의 명세가 이미 있으면 새로 만들지 않고 덮어쓴다.
- 기존 명세를 덮어쓸 때는 바뀐 부분(추가·삭제·변경된 필드나 응답)을 PR 본문에 적는다.

## 작업 순서

1. `docs/api/` 에 같은 API의 명세가 이미 있는지 확인한다.
2. 요청에서 기능명과 필요한 API를 정리한다.
3. 아래 작성 규칙과 템플릿대로 명세를 작성한다.
4. 다 쓴 뒤 작성 규칙을 하나씩 대조해 빠진 것이 없는지 확인한다.

## 작성 규칙

- 아래 템플릿의 섹션 순서와 표 형식(이름 | 데이터타입 | 필수 | 설명)을 그대로 따른다.
- 해당 내용이 없는 섹션(예: request headers)도 지우지 않고 표에 `없음` 이라고 적는다.
- URL은 `/api` 로 시작하고, 복수형 명사, 소문자, 케밥 케이스를 쓴다: `/api/members`, `/api/order-items`
- 동사를 URL에 넣지 않는다: `/api/createMember` ❌ → `POST /api/members` ✅
- Path Variable 은 중괄호와 camelCase: `/api/members/{memberId}`
- 성공 응답은 감싸지 않고 결과 필드를 바로 반환한다.
- 성공 응답 상태 코드: 생성(POST)은 `201 Created`, 조회·수정은 `200 OK`, 삭제는 `204 No Content` 를 쓴다.
- 이미 구현된 API는 코드와 이 규칙이 다르면 코드 기준으로 쓰고, 다른 부분을 PR 본문에 적는다.
- 파일 업로드 API는 request headers 에 `Content-Type: multipart/form-data` 를 적고, request body 는 JSON 대신 폼 필드(`file` 등) 표로 적는다.
- 에러 응답은 `{ "status", "code", "message", "timestamp" }` 형식으로, 상황별로 하나씩 적는다. `code` 는 `ErrorCode` 의 이름이다.
- 입력 검증이 필요한 API는 `400 Bad Request` 에러 응답도 적는다. 이때는 `validationErrors`(`field`, `message`) 가 추가된다. 검증 규칙이 요구사항에 없으면
  `결정 필요` 로 둔다.
- 날짜·시간은 ISO 8601 문자열(`2026-11-17T10:00:00`)로 쓴다.
- 응답 표의 "필수"는 항상 포함되는 필드면 `Y`, 없을 수 있으면 `N` 으로 적는다.
- 요구사항에 없는 정책(검증 규칙, 권한 등)은 추측하지 않고 설명 칸에 `결정 필요` 라고 적고, PR 본문의 "결정 필요" 항목에도 모은다.

## 템플릿

````markdown
# <API 이름>

domain: <도메인>

url: /api/<리소스>/{<식별자>}

method: <GET | POST | PATCH | PUT | DELETE>

# 01. 설명

<이 API를 언제 쓰는지 한두 문장>

- <필수 사항>
- <선택 사항>

# 02. 요청(Request)

## a. Parameter & Querystring

```txt
/api/members/{memberId}
/api/members/1
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| memberId | Long | Y | 회원 식별자 |

## b. request headers

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| 없음 | | | |

## c. request body

```json
{
  "name": "gygim"
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| name | String | Y | 변경할 회원 이름 |

# 03. 응답(Response)

## a. response header

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| 없음 | | | |

## b. response body

**성공 응답:** `200 OK`

```json
{
  "id": 1,
  "updatedAt": "2026-11-17T10:00:00"
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| id | Long | Y | 수정된 회원 식별자 |
| updatedAt | String | Y | 수정 일시 |

**에러 응답:** `404 Not Found` — 회원이 없을 때

```json
{
  "status": 404,
  "code": "MEMBER_NOT_FOUND",
  "message": "member not found",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `400 Bad Request` — 입력 검증에 실패했을 때

```json
{
  "status": 400,
  "code": "INVALID_INPUT",
  "message": "invalid input",
  "timestamp": "2026-11-17T10:00:00",
  "validationErrors": [
    { "field": "name", "message": "must not be blank" }
  ]
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| status | Integer | Y | HTTP 상태 코드 |
| code | String | Y | 에러 코드(`ErrorCode` 이름) |
| message | String | Y | 에러 메시지 |
| timestamp | String | Y | 에러 발생 일시 |
| validationErrors | Array | N | 검증 실패 목록. 400 검증 오류일 때만 포함 |
| validationErrors[].field | String | Y | 검증에 실패한 필드명 |
| validationErrors[].message | String | Y | 검증 실패 사유 |
````