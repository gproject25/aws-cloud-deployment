package com.awsclouddeployment.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

	//모든 APi에 대해서 실행
	@Around("execution(* com.awsclouddeployment.controller..*(..))")
	public Object logApiRequest(ProceedingJoinPoint joinPoint) throws Throwable {
		//실행 전
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

		log.info("[API - LOG] {} {} - start", request.getMethod(), request.getRequestURI());

		Object result = joinPoint.proceed();

		//실행 후
		log.info("[API - LOG] {} {} - success", request.getMethod(), request.getRequestURI());

		return result;
	}
}
