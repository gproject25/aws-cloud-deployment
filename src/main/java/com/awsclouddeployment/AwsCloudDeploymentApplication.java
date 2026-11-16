package com.awsclouddeployment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AwsCloudDeploymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsCloudDeploymentApplication.class, args);
	}

}
