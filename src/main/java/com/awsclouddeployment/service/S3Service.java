package com.awsclouddeployment.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.awsclouddeployment.common.exception.ErrorCode;
import com.awsclouddeployment.common.exception.custom.FileUploadFailException;
import com.awsclouddeployment.service.s3.S3Properties;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Template s3Template;
	private final S3Properties s3Properties;

	private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofDays(7);

	public String upload(Long memberId, MultipartFile file) {
		try {
			String key = "profile-images/" +  memberId + "/" +  UUID.randomUUID() + "_" + file.getOriginalFilename();

			try (InputStream inputStream = file.getInputStream()) {
				s3Template.upload(s3Properties.getBucket(), key, inputStream);
			}

			return key;

		} catch (IOException e) {
			throw new FileUploadFailException();
		}
	}

	public URL getDownloadUrl(String key) {
		return s3Template.createSignedGetURL(s3Properties.getBucket(), key, PRESIGNED_URL_EXPIRATION);
	}
}