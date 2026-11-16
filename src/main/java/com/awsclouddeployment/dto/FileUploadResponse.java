package com.awsclouddeployment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileUploadResponse {
	private final String fileName;
	private final String fileKey;
}