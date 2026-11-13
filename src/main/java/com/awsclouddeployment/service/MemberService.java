package com.awsclouddeployment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.awsclouddeployment.dto.MemberResponse;
import com.awsclouddeployment.dto.MemberSaveRequest;
import com.awsclouddeployment.entity.Member;
import com.awsclouddeployment.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	@Transactional
	public MemberResponse saveMember(MemberSaveRequest request) {
		Member member = new Member(request.getName(), request.getAge(), request.getMbti());
		return new MemberResponse(memberRepository.save(member));
	}

	@Transactional(readOnly = true)
	public MemberResponse getMember(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new IllegalArgumentException("없는 팀원")
		);
		return new MemberResponse(member);
	}
}
