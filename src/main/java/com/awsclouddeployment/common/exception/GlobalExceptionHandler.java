package com.awsclouddeployment.common.exception;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.awsclouddeployment.common.exception.custom.FileUploadFailException;
import com.awsclouddeployment.common.exception.custom.FileUrlGenerationException;
import com.awsclouddeployment.common.exception.custom.MemberNotFoundException;
import com.awsclouddeployment.common.exception.custom.ProfileImageNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
		HttpMessageNotReadableException.class,
		MethodArgumentTypeMismatchException.class,
		MissingServletRequestParameterException.class
	})
	public ResponseEntity<ErrorResponse> handleHttpFormatException(HttpServletRequest request, Exception e) {
		printErrorLog(request, e);
		return buildResponseEntity(ErrorCode.REQUEST_FORMAT_INVALID, null);

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(HttpServletRequest request, MethodArgumentNotValidException e) {
		printErrorLog(request, e);
		List<FieldErrorResponse> validationErrors = e.getBindingResult().getFieldErrors().stream().map(error ->
				new FieldErrorResponse(
					error.getField(),
					error.getDefaultMessage()
				)).toList();

		return buildResponseEntity(ErrorCode.REQUEST_VALIDATION_FAILED, validationErrors);
	}

	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleMemberNotFoundException(HttpServletRequest request, MemberNotFoundException e) {
		printErrorLog(request, e);
		return buildResponseEntity(ErrorCode.MEMBER_NOT_FOUND, null);
	}

	@ExceptionHandler(FileUploadFailException.class)
	public ResponseEntity<ErrorResponse> handleFileUploadFailException(HttpServletRequest request, FileUploadFailException e) {
		printErrorLog(request, e);
		return buildResponseEntity(ErrorCode.FILE_UPLOAD_FAILED, null);
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ErrorResponse> handleMultipartException(HttpServletRequest request, MultipartException e) {
		printErrorLog(request, e);
		return buildResponseEntity(ErrorCode.MULTIPART_REQUEST_INVALID, null);
	}

	@ExceptionHandler(ProfileImageNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleProfileImageNotFoundException(HttpServletRequest request, ProfileImageNotFoundException e) {
		printErrorLog(request, e);
		return buildResponseEntity(ErrorCode.PROFILE_IMAGE_NOT_FOUND, null);
	}

	@ExceptionHandler(FileUrlGenerationException.class)
	public ResponseEntity<ErrorResponse> handleFileUrlGenerationException(HttpServletRequest request, FileUrlGenerationException e) {
		printErrorLog(request, e);
		return buildResponseEntity(ErrorCode.FILE_URL_GENERATION_FAILED, null);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
		printErrorLog(request, e);

		return buildResponseEntity(ErrorCode.METHOD_NOT_ALLOWED, null);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(HttpServletRequest request) {
		log.error("[API - ERROR] {} {}",
			request.getMethod(),
			request.getRequestURI()
		);
		return buildResponseEntity(ErrorCode.REQUEST_URL_INVALID, null);
	}

	//default
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedException(HttpServletRequest request, Exception e) {
		printErrorLog(request, e);

		return buildResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR, null);
	}

	private ResponseEntity<ErrorResponse> buildResponseEntity(ErrorCode errorCode, List<FieldErrorResponse> validationErrors) {
		return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponse(errorCode, validationErrors));
	}

	private void printErrorLog(HttpServletRequest request, Exception e) {
		log.error("[API - ERROR] {} {}",
			request.getMethod(),
			request.getRequestURI(),
			e
		);
	}

}
