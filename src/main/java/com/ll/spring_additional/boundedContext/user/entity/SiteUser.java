package com.ll.spring_additional.boundedContext.user.entity;

import com.ll.spring_additional.standard.util.Ut;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	public String getJdenticon() {
		return Ut.hash.sha256(this.username);
	}

	public boolean isAdmin() {
		return this.username.equals("admin");
	}
}