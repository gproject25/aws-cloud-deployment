package com.awsclouddeployment.service.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "app.aws.s3")
public class S3Properties {

	private final String bucket;

	public S3Properties(String bucket) {
		this.bucket = bucket;
	}
}
