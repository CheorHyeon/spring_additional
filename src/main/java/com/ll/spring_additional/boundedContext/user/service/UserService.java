package com.ll.spring_additional.boundedContext.user.service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.base.exception.DataNotFoundException;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;
import com.ll.spring_additional.boundedContext.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JavaMailSender mailSender;

	private static final String ADMIN_ADDRESS = "r4560798@naver.com";

	// private final Executor executor;

	@Transactional
	public SiteUser create(String username, String email, String password) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
		return user;
	}

	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = userRepository.findByusername(username);
		if (siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
	}

	@Transactional
	public String setTemporaryPW(SiteUser user) {
		// 임시 비밀번호 생성 및 암호화
		String temporaryPassword = createRandomPassword();
		user.setPassword(passwordEncoder.encode(temporaryPassword));
		userRepository.save(user);
		return temporaryPassword;
	}

	@Async // 비동기
	public void sendEmail(String email, String userName, String tempPW) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setFrom(ADMIN_ADDRESS);
		message.setSubject(userName+"님의 임시비밀번호 안내 메일입니다.");
		message.setText("안녕하세요 "+userName+"님의 임시 비밀번호는 [" + tempPW +"] 입니다.");

		mailSender.send(message);

	}

	private String createRandomPassword() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		String generatedString = random.ints(leftLimit, rightLimit + 1)
			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			.limit(targetStringLength)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();

		return generatedString;
	}

	@Transactional
	public void updatePassWord(SiteUser user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
}