package com.awsclouddeployment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awsclouddeployment.dto.MemberResponse;
import com.awsclouddeployment.dto.MemberSaveRequest;
import com.awsclouddeployment.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
	private final MemberService memberService;

	@PostMapping
	public ResponseEntity<MemberResponse> saveMember(@RequestBody MemberSaveRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(memberService.saveMember(request));
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId){
		return ResponseEntity.ok(memberService.getMember(memberId));
	}
}
