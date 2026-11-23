package com.awsclouddeployment.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.awsclouddeployment.common.exception.custom.FileDeleteFailException;
import com.awsclouddeployment.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileImageEventListener {
	private final S3Service s3Service;

	// 기존 이미지 삭제: DB 커밋 성공 이후
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProfileImageReplaced(ProfileImageReplacedEvent event) {
		try {
			s3Service.deleteFile(event.oldImageKey());
		} catch (FileDeleteFailException e) {
			log.error("파일 삭제를 실패했습니다. {}", event.oldImageKey(), e);
		}
	}

	// 새 이미지 삭제: DB 롤백 경우
	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void handleProfileImageUploaded(ProfileImageUploadedEvent event) {
		try {
			s3Service.deleteFile(event.newImageKey());
		} catch (FileDeleteFailException e) {
			log.error("새 이미지 롤백 처리 중 삭제에 실패했습니다. {}", event.newImageKey(), e);
		}
	}
}
