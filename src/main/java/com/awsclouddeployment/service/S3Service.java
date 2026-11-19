package com.awsclouddeployment.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.exception.SdkException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.awsclouddeployment.common.exception.custom.FileUploadFailException;
import com.awsclouddeployment.common.exception.custom.FileUrlGenerationException;
import com.awsclouddeployment.service.AWSProperties.CloudFrontProperties;
import com.awsclouddeployment.service.AWSProperties.S3Properties;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Template s3Template;
	private final S3Properties s3Properties;
	private final CloudFrontProperties cloudFrontProperties;

	private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofDays(7);

	public String upload(Long memberId, MultipartFile file) {
		try {
			String key = "profile-images/" +  memberId + "/" +  UUID.randomUUID() + "_" + file.getOriginalFilename();

			try (InputStream inputStream = file.getInputStream()) {
				s3Template.upload(s3Properties.getBucket(), key, inputStream);
			}

			return key;

		} catch (IOException | SdkException e) {
			throw new FileUploadFailException();
		}
	}

	public URL getPresignedUrl(String key) {
		try{
			return s3Template.createSignedGetURL(s3Properties.getBucket(), key, PRESIGNED_URL_EXPIRATION);
		} catch (SdkException e) {
			throw new FileUrlGenerationException();
		}
	}

	public String getCloudfrontUrl(String key) {
		return cloudFrontProperties.getDomain() + "/" + key;
	}
}