package com.ll.spring_additional.base.initData;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.answer.service.AnswerService;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.question.service.QuestionService;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;
import com.ll.spring_additional.boundedContext.user.service.UserService;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
	@Bean
	CommandLineRunner initData(
		PasswordEncoder passwordEncoder,
		QuestionService questionService,
		UserService userService,
		AnswerService answerService
	)
	{
		return new CommandLineRunner() {
			@Override
			@Transactional
			public void run(String... args) throws Exception {

				userService.create("admin","admin@test.com", "1234");

				SiteUser user1 = userService.create("user1", "user1@test.com", "1234");
				SiteUser user2 = userService.create("user2", "user2@test.com", "1234");

				for (int i = 1; i <= 300; i++) {
						String subject = String.format("테스트 데이터입니다:[%03d]", i);
						String content = "내용무";
						questionService.create(subject, content, user1);
					}

				Question question1 = questionService.create("질문입니닷", "질문이에요!", user2);
				Question question2 = questionService.create("질문입니닷22", "질문이에요!22", user2);

				Answer answer1 = answerService.create(question1, "답변1", user1);
				Answer answer2 = answerService.create(question1, "답변2", user1);

				}
			};
	}
}