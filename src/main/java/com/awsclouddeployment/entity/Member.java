package com.awsclouddeployment.entity;

import com.awsclouddeployment.entity.enums.Mbti;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false)
	private int age;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Mbti mbti;

	@Column(name = "profile_image_key")
	private String profileImageKey;

	public Member(String name, int age, Mbti mbti) {
		this.name = name;
		this.age = age;
		this.mbti = mbti;
	}

	public void updateProfileImage(String profileImageKey) {
		this.profileImageKey = profileImageKey;
	}
}
