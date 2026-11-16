package com.awsclouddeployment.dto;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImageUploadRequest {

	@NotNull(message = "업로드할 파일을 선택해주세요.")
	private MultipartFile file;

	@AssertTrue(message = "빈 파일은 업로드할 수 없습니다.")
	public boolean isFileNotEmpty() {
		return file == null || !file.isEmpty();
	}

	@AssertTrue(message = "이미지 파일은 10MB 이하만 업로드할 수 있습니다.")
	public boolean isFileSizeValid() {
		return file == null || file.getSize() <= 10 * 1024 * 1024;
	}

	@AssertTrue(message = "JPEG, PNG, WebP 형식의 이미지만 업로드할 수 있습니다.")
	public boolean isFileTypeValid() {
		if (file == null || file.isEmpty()) {
			return true;
		}

		return Set.of("image/jpeg", "image/png", "image/webp").contains(file.getContentType());
	}
}
