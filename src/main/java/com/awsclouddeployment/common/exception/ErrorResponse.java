package com.awsclouddeployment.common.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class ErrorResponse {
	private final int status;
	private final String code;
	private final String message;
	private final LocalDateTime timestamp;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final List<FieldErrorResponse> validationErrors;

	//validation
	public ErrorResponse(ErrorCode errorCode, List<FieldErrorResponse> validationErrors) {
		this.status = errorCode.getStatus().value();
		this.code = errorCode.name();
		this.message = errorCode.getMessage();
		this.timestamp = LocalDateTime.now();
		this.validationErrors = validationErrors;
	}
}
