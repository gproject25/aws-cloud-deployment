package com.awsclouddeployment.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.awsclouddeployment.common.exception.custom.MemberNotFoundException;
import com.awsclouddeployment.common.exception.custom.ProfileImageNotFoundException;
import com.awsclouddeployment.dto.FileDownloadUrlResponse;
import com.awsclouddeployment.dto.FileUploadResponse;
import com.awsclouddeployment.dto.MemberResponse;
import com.awsclouddeployment.dto.MemberSaveRequest;
import com.awsclouddeployment.entity.Member;
import com.awsclouddeployment.repository.MemberRepository;
import com.awsclouddeployment.listener.ProfileImageReplacedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final S3Service s3Service;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public MemberResponse saveMember(MemberSaveRequest request) {
		Member member = new Member(request.getName(), request.getAge(), request.getMbti());
		return new MemberResponse(memberRepository.save(member));
	}

	@Transactional(readOnly = true)
	public MemberResponse getMember(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new MemberNotFoundException()
		);
		return new MemberResponse(member);
	}

	@Transactional
	public FileUploadResponse uploadProfileImage(Long memberId, MultipartFile file) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new MemberNotFoundException()
		);

		//기존 Image 가져오기 + 삭제
		String oldImageKey = member.getProfileImageKey();

		//새 이미지 업로드
		String fileKey = s3Service.upload(memberId, file);
		member.updateProfileImage(fileKey);

		// 트랜잭션 커밋 이후 기존 이미지 삭제
		if (oldImageKey != null) {
			applicationEventPublisher.publishEvent(
				new ProfileImageReplacedEvent(oldImageKey)
			);
		}

		return new FileUploadResponse(file.getOriginalFilename(), fileKey);
	}

	@Transactional(readOnly = true)
	public FileDownloadUrlResponse getProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new MemberNotFoundException()
		);

		String fileKey = member.getProfileImageKey();
		if (fileKey == null) {
			throw new ProfileImageNotFoundException();
		}

		//Presigned 방식:
		//URL downloadUrl = s3Service.getPresignedUrl(filekey);

		//CloudFront 방식:
		String downloadUrl = s3Service.getCloudfrontUrl(fileKey);
		return new FileDownloadUrlResponse(downloadUrl);
	}
}
