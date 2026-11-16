package com.awsclouddeployment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awsclouddeployment.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
