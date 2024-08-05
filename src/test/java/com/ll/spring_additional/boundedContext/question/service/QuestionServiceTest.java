package com.ll.spring_additional.boundedContext.question.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ll.spring_additional.base.exception.DataNotFoundException;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.question.questionEnum.QuestionEnum;
import com.ll.spring_additional.boundedContext.question.repository.QuestionRepository;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
	@InjectMocks
	private QuestionService questionService;

	@Mock
	private QuestionRepository questionRepository;

	@Test
	@DisplayName("질문 조회 메서드 테스트")
	void testGetList() {
		// 데이터 설정
		Page<Question> page = createTestPage();

		// 모킹 설정
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createDate")));
		given(questionRepository.findAllByKeywordAndType("테스트", QuestionEnum.QNA.getStatus(), pageable))
			.willReturn(page);

		// 서비스 메서드 호출 및 검증
		Page<Question> temp = questionService.getList(QuestionEnum.QNA.getStatus(), 0, "테스트");

		Assertions.assertEquals(temp.getTotalElements(), 2);
		Assertions.assertEquals(temp.getContent().get(0).getSubject(), "테스트1");
		Assertions.assertEquals(temp.getContent().get(1).getSubject(), "테스트2");
	}

	private Page<Question> createTestPage() {
		Question question1 = new Question();
		question1.setId(1L);
		question1.setSubject("테스트1");
		question1.setCategory(QuestionEnum.QNA.getStatus());
		question1.setCreateDate(LocalDateTime.now());

		Question question2 = new Question();
		question2.setId(2L);
		question2.setSubject("테스트2");
		question2.setCategory(QuestionEnum.QNA.getStatus());
		question2.setCreateDate(LocalDateTime.now());

		List<Question> questions = Arrays.asList(question1, question2);
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createDate")));
		return new PageImpl<>(questions, pageable, questions.size());
	}

	@Test
	@DisplayName("Id로 질문 가져오기 - 해당 Id 질문이 있는 경우")
	void testGetQuestion() {
		// 데이터 설정
		Question question1 = new Question();
		question1.setId(1L);
		question1.setSubject("테스트1");
		question1.setCategory(QuestionEnum.QNA.getStatus());
		question1.setCreateDate(LocalDateTime.now());

		// 모킹 설정
		given(questionRepository.findById(1L))
			.willReturn(Optional.of(question1));

		// 서비스 메서드 호출 및 검증
		Question q = questionService.getQuestion(1L);

		Assertions.assertEquals(q.getSubject(), "테스트1");
		Assertions.assertEquals(q.getId(), 1L);
		Assertions.assertEquals(q.getCategory(), QuestionEnum.QNA.getStatus());

		// Repository 메서드 호출 여부 검사
		verify(questionRepository).findById(1L);
	}

	@Test
	@DisplayName("Id로 질문 가져오기 - 질문이 존재하지 않는 경우 예외 발생")
	void testGetQuestion_WhenQuestionDoesNotExist() {
		// 모킹 설정
		given(questionRepository.findById(2L)).willReturn(Optional.empty());

		// 서비스 메서드 호출 및 예외 검증
		assertThrows(DataNotFoundException.class, () -> questionService.getQuestion(2L));

		// Repository 메서드 호출 여부 검사
		verify(questionRepository).findById(2L);
	}

}
