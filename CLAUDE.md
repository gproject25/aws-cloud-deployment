# 작업 지침서

## 프로젝트 개요

- Spring Boot / Java 17 기반 REST API 서버
- 모든 명세는 'docs/'에 있다
- 폴더별 역할은 `docs/README.md` 를 본다

## 절대 하면 안 되는 일

- main 브랜치에 직접 커밋하지 않는다
- .env와 .yml 설정 파일을 읽거나 수정하지 않는다
- `git push --force` 를 사용하지 않는다
- 기존 기능을 임의로 삭제하거나 변경하지 않는다

## 작업 전

- 작업할 문서 폴더의 `README.md`(작성 지침서)를 먼저 읽는다
- 작업 브랜치를 만든다: `docs/api-<기능명>` (예: `docs/api-signup`)

## 작업 중

- 문서 형식은 각 폴더 `README.md` 의 템플릿을 그대로 따른다
- 요구사항이 모호하면 추측하지 말고 "결정 필요" 항목으로 남긴다

## 작업 후

- 커밋 메시지는 Conventional Commits 형식을 따른다.
    - feat: <기능 설명>
    - fix: <수정 내용>
    - refactor: <리팩토링 내용>
    - docs: <기능명> API 명세 추가
- push 후 `gh pr create` 로 PR을 올린다. base는 main.
- PR 본문에 변경 내용과 "결정 필요" 항목을 적는다
- PR 리뷰를 반영하면 각 리뷰 코멘트에 반영 내용과 커밋을 답글로 남긴다