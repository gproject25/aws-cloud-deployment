package com.awsclouddeployment.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.awsclouddeployment.service.AWSProperties.S3Properties;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {
}
