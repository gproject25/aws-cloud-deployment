package com.awsclouddeployment.service.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloudfront")
public class CloudFrontProperties {
	private String domain;
}
