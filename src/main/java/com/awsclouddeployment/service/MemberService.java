package com.awsclouddeployment.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.awsclouddeployment.common.annotations.S3PerformanceLog;
import com.awsclouddeployment.common.exception.custom.FileDeleteFailException;
import com.awsclouddeployment.common.exception.custom.MemberNotFoundException;
import com.awsclouddeployment.common.exception.custom.ProfileImageNotFoundException;
import com.awsclouddeployment.dto.FileDownloadUrlResponse;
import com.awsclouddeployment.dto.FileUploadResponse;
import com.awsclouddeployment.dto.MemberResponse;
import com.awsclouddeployment.dto.MemberSaveRequest;
import com.awsclouddeployment.entity.Member;
import com.awsclouddeployment.entity.enums.Mbti;
import com.awsclouddeployment.listener.ProfileImageReplacedEvent;
import com.awsclouddeployment.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final S3Service s3Service;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final TransactionTemplate transactionTemplate;

	@Transactional
	public MemberResponse saveMember(MemberSaveRequest request) {
		Member member = new Member(request.getName(), request.getAge(), Mbti.valueOf(request.getMbti()));
		return new MemberResponse(memberRepository.save(member));
	}

	@Transactional(readOnly = true)
	public MemberResponse getMember(Long memberId) {
		Member member = findMemberById(memberId);
		return new MemberResponse(member);
	}

	//@Transactional
	@S3PerformanceLog
	public FileUploadResponse uploadProfileImage(Long memberId, MultipartFile file) {
		if (!memberRepository.existsById(memberId)) {
			throw new MemberNotFoundException();
		}

		//새 이미지 업로드
		String fileKey = s3Service.upload(memberId, file);

		// 짧은 트랜잭션으로 DB 갱신
		try {
			transactionTemplate.executeWithoutResult(status -> {
				Member member = memberRepository.findById(memberId)
					.orElseThrow(MemberNotFoundException::new);

				String oldImageKey = member.getProfileImageKey();
				member.updateProfileImage(fileKey);

				//  AFTER_COMMIT 리스너가 그대로 동작함
				if (oldImageKey != null) {
					applicationEventPublisher.publishEvent(new ProfileImageReplacedEvent(oldImageKey));
				}
			});
		} catch (RuntimeException e) {
			try {
				s3Service.deleteFile(fileKey);
			} catch (FileDeleteFailException deleteEx) {
				e.addSuppressed(deleteEx);  // 원래 예외는 유지하고, 삭제 실패는 함께 기록
			}
			throw e;
		}

		return new FileUploadResponse(file.getOriginalFilename(), fileKey);
	}

	@S3PerformanceLog
	@Transactional(readOnly = true)
	public FileDownloadUrlResponse getProfileImage(Long memberId) {
		Member member = findMemberById(memberId);
		String fileKey = member.getProfileImageKey();
		if (fileKey == null) {
			throw new ProfileImageNotFoundException();
		}

		//Presigned 방식:
		//URL presignedUrl = s3Service.getPresignedUrl(fileKey);
		//String downloadUrl = presignedUrl.toString();

		//CloudFront 방식:
		String downloadUrl = s3Service.getCloudfrontUrl(fileKey);
		return new FileDownloadUrlResponse(downloadUrl);
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(
			() -> new MemberNotFoundException()
		);
	}
}
