package com.awsclouddeployment.dto;

import lombok.Getter;

@Getter
public class MemberSaveRequest {
	//validation 추가

	private String name;
	private int age;
	private String mbti;
}
