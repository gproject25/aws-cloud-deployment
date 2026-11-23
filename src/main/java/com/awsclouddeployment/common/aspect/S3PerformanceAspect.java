package com.awsclouddeployment.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class S3PerformanceAspect {
	//S3 upload 성능 검증용
	@Around("@annotation(com.awsclouddeployment.common.annotations.S3PerformanceLog)")
	public Object measureS3Log(ProceedingJoinPoint joinPoint) throws Throwable {
		//실행 전
		String methodName = joinPoint.getSignature().getName();
		long startTime = System.nanoTime();

		Object result = joinPoint.proceed();

		//실행 후
		long executionTime = (System.nanoTime() - startTime) / 1_000_000;
		log.info("[S3 - PERFORMANCE] {} - execution time: {} ms", joinPoint.getSignature().getName(), executionTime);

		return result;
	}

}
