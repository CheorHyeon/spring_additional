package com.ll.spring_additional.base.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // 스프링 환경설정 파일
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 -> 내부적으로 시큐리티 필터체인 동작
@EnableMethodSecurity(prePostEnabled = true)  // @PreAuthorize 사용하기 위해 필요
public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.formLogin(
				formLogin -> formLogin
					.loginPage("/user/login")
					.defaultSuccessUrl("/")
			)

			.logout(
				logout -> logout
					.logoutUrl("/user/logout")
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
				// 로그아웃 시 세션 무효화, false 시 로그인 마다 세션 생성 -> 여러 세션 유효화
				// default 값 true
			);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}