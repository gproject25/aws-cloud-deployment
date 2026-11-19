package com.awsclouddeployment.service.AWSProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.aws.s3")
public class S3Properties {

	private String bucket;
}