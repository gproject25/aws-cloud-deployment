package com.awsclouddeployment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.awsclouddeployment.dto.FileDownloadUrlResponse;
import com.awsclouddeployment.dto.FileUploadResponse;
import com.awsclouddeployment.dto.MemberResponse;
import com.awsclouddeployment.dto.MemberSaveRequest;
import com.awsclouddeployment.dto.ProfileImageUploadRequest;
import com.awsclouddeployment.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Validated
public class MemberController {
	private final MemberService memberService;

	@PostMapping
	public ResponseEntity<MemberResponse> saveMember(@Valid @RequestBody MemberSaveRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(memberService.saveMember(request));
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId){
		return ResponseEntity.ok(memberService.getMember(memberId));
	}

	//Profile Image

	@PostMapping("/{memberId}/profile-image")
	public ResponseEntity<FileUploadResponse> uploadProfileImage(@PathVariable Long memberId,@Valid @ModelAttribute ProfileImageUploadRequest request){
		return ResponseEntity.ok(memberService.uploadProfileImage(memberId, request.getFile()));
	}

	@GetMapping("/{memberId}/profile-image")
	public ResponseEntity<FileDownloadUrlResponse> getProfileImage(@PathVariable Long memberId){
		return ResponseEntity.ok(memberService.getProfileImage(memberId));
	}
}
