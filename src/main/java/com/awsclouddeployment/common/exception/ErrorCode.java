package com.awsclouddeployment.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	REQUEST_FORMAT_INVALID(
		HttpStatus.BAD_REQUEST,
		"잘못된 요청 형식입니다."
	),
	REQUEST_VALIDATION_FAILED(
		HttpStatus.BAD_REQUEST,
		"요청 데이터 검증에 실패했습니다."
	),
	REQUEST_URL_INVALID(
		HttpStatus.BAD_REQUEST,
		"URL 경로가 잘못되었습니다."
	),

	MEMBER_NOT_FOUND(
		HttpStatus.NOT_FOUND,
		"회원을 찾을 수 없습니다."
	),
	METHOD_NOT_ALLOWED(
		HttpStatus.METHOD_NOT_ALLOWED,
		"지원하지 않는 HTTP 메서드입니다."
	),
	INTERNAL_SERVER_ERROR(
		HttpStatus.INTERNAL_SERVER_ERROR,
		"서버 내부 오류가 발생했습니다."
	)


	;

	private final HttpStatus status;
	private final String message;
}
