package com.ll.spring_additional.boundedContext.user.userRole;

import lombok.Getter;

@Getter
public enum UserRole {
	ADMIN("ROLE_ADMIN"),
	USER("ROLE_USER");

	UserRole(String value) {
		this.value = value;
	}

	private String value;
}