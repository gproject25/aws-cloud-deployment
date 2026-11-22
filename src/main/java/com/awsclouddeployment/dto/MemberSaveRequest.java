package com.awsclouddeployment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberSaveRequest {
	//validation 추가

	@NotBlank(message = "이름은 필수 입력 항목입니다.")
	@Size(max = 50, message = "이름은 50자 이하여야 합니다.")
	private String name;

	@NotNull(message = "나이는 필수 입력 항목입니다.")
	@Min(value = 1, message = "나이는 1살 이상이어야 합니다.")
	@Max(value = 150, message = "나이는 150살 이하여야 합니다.")
	private Integer age;

	@NotBlank(message = "MBTI는 필수 입력 항목입니다.")
	@Pattern(
		regexp = "^(|ISTJ|ISFJ|INFJ|INTJ|ISTP|ISFP|INFP|INTP|ESTP|ESFP|ENFP|ENTP|ESTJ|ESFJ|ENFJ|ENTJ)$",
		message = "올바른 MBTI 유형을 입력해주세요."
	)
	private String mbti;
}
