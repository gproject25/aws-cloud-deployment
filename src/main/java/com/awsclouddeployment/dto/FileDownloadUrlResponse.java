package com.awsclouddeployment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileDownloadUrlResponse {
	private final String url;
}