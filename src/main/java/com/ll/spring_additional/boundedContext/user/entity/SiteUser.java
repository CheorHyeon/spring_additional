package com.ll.spring_additional.boundedContext.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

	private String email;

	private String providerTypeCode;

	public String getJdenticon() {
		return Ut.hash.sha256(this.username);
	}

	public boolean isAdmin() {
		return this.username.equals("admin");
	}

	public List<? extends GrantedAuthority> getGrantedAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		grantedAuthorities.add(new SimpleGrantedAuthority("member"));

		if ("admin".equals(username)) {
			grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
		}

		return grantedAuthorities;
	}
}