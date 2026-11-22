# 프로필 이미지 조회

domain: member

url: /api/members/{memberId}/profile-image

method: GET

# 01. 설명

회원의 프로필 이미지를 내려받을 수 있는 URL 을 조회할 때 사용한다. 이미지 파일 자체가 아니라 CloudFront 기반 URL 을 반환한다.

- 필수 사항: `memberId`
- 선택 사항: 없음
- 프로필 이미지가 등록되지 않은 회원은 404 를 반환한다.
- URL 만료 여부: 코드상 CloudFront URL 은 `{도메인}/{fileKey}` 형태로 단순 조합되며 만료 정책은 확인되지 않음. 결정 필요
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
| 없음 | | | |

## c. request body

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| 없음 | | | |

# 03. 응답(Response)

## a. response header

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| 없음 | | | |

## b. response body

**성공 응답:** `200 OK`

```json
{
  "url": "https://cdn.example.com/profile-images/1/3f2b8c1e-0000-0000-0000-000000000000_profile.png"
}
```

| 이름 | 데이터타입 | 필수 | 설명 |
|---|---|---|---|
| url | String | Y | 프로필 이미지 다운로드 URL (CloudFront 도메인 + `/` + 파일 키) |

**에러 응답:** `404 Not Found` — 회원이 없을 때

```json
{
  "status": 404,
  "code": "MEMBER_NOT_FOUND",
  "message": "팀원을 찾을 수 없습니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `404 Not Found` — 회원은 있으나 프로필 이미지가 등록되지 않았을 때

```json
{
  "status": 404,
  "code": "PROFILE_IMAGE_NOT_FOUND",
  "message": "프로필 사진을 찾을 수 없습니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `400 Bad Request` — `memberId` 가 Long 으로 변환되지 않을 때

```json
{
  "status": 400,
  "code": "REQUEST_FORMAT_INVALID",
  "message": "잘못된 요청 형식입니다.",
  "timestamp": "2026-11-17T10:00:00"
}
```

**에러 응답:** `500 Internal Server Error` — 조회 중 예상하지 못한 오류가 발생했을 때

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
