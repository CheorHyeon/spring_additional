package com.ll.spring_additional.boundedContext.user.service;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserService userService;

	// 카카오톡 로그인이 성공할 때 마다 이 함수가 실행된다.
	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// access 토큰으로 user 정보 조회
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String oauthId = oAuth2User.getName();

		String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

		String username = providerTypeCode + "__%s".formatted(oauthId);

		SiteUser siteUser = userService.whenSocialLogin(providerTypeCode, username);

		return new CustomOAuth2User(siteUser.getUsername(), siteUser.getPassword(), siteUser.getGrantedAuthorities());
	}
}

class CustomOAuth2User extends User implements OAuth2User {

	public CustomOAuth2User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public String getName() {
		return getUsername();
	}
}