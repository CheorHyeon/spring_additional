package com.ll.spring_additional.boundedContext.answer.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.answer.service.AnswerService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AnswerControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private AnswerService answerService;

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /answer/create/{id} 는 질문에 대한 답변을 생성한다.")
	void t001() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/answer/create/3")
				.with(csrf()) // CSRF 키 생성
				.param("content", "테스트답변1"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("createAnswer"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/question/detail/3#answer*")); // 생성한 답변 앵커로 이동
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /answer/create/{id} 는 질문에 대한 답변 내용을 입력하지 않으면 답변 등록 폼과 함께 오류 메세지를 출력한다.")
	void t002() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/answer/create/3")
				.with(csrf())) // CSRF 키 생성
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("createAnswer"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<h5 class="my-3 border-bottom pb-2">답변 등록</h5>
					                  """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<div>내용은 필수항목입니다.</div>
					                  """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /answer/modify/{id} 는 답변을 수정할 폼을 보여준다")
	void t003() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/answer/modify/1"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerModify"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<h5 class="my-3 border-bottom pb-2">답변 수정</h5>
				                  """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<label for="content" class="form-label">내용</label>
				          """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<textarea class="form-control" rows="10" id="content" name="content">답변1</textarea>
				          """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("GET /answer/modify/{id} 는 본인이 작성한 답변이 아닌 경우 수정폼을 보여주지 않는다.")
	void t004() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/answer/modify/1"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerModify"))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("수정권한이 없습니다."));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /answer/modify/{id} 는 답변을 수정한다.")
	void t005() throws Exception {
		Answer beforeAnswer = answerService.getAnswer(1);
		String beforeContent = beforeAnswer.getContent();

		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/answer/modify/1")
				.with(csrf())
				.param("content", "이건 수정이여"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerModify"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/question/detail/*"));

		Answer afterAnswer = answerService.getAnswer(1);
		String afterContent = afterAnswer.getContent();

		assertThat(afterContent).isEqualTo("이건 수정이여"); // 수정 됐는지 확인
		assertThat(beforeContent).isNotEqualTo(afterContent); // 수정 전과 다른지 확인
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("POST /answer/modify/{id} 는 본인이 작성한 답변이 아닌경우 수정 불가")
	void t006() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/answer/modify/1")
				.with(csrf())
				.param("content", "수정되나?"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerModify"))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("수정권한이 없습니다."));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /answer/modify/{id} 는 답변 내용을 작성하지 않을 경우 수정이 되지 않는다.")
	void t007() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/answer/modify/1")
				.with(csrf()))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerModify"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<h5 class="my-3 border-bottom pb-2">답변 수정</h5>
					                  """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<div>내용은 필수항목입니다.</div>
					                  """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("DELETE /answer/{id} 는 답변을 삭제한다.")
	void t008() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(delete("/answer/1")
				.with(csrf()))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerDelete"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/question/detail/*")); // 삭제 후 질문 상세로 가는지 테스트
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("Delete /answer/{id} 는 본인이 작성하지 않은 답변은 삭제할 수 없다.")
	void t009() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(delete("/answer/1")
				.with(csrf()))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerDelete"))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("삭제권한이 없습니다."));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("GET /answer/vote/{id} 는 답변을 추천할 수 있다. Set으로 관리되기에 중복 추천이 안됨")
	void t010() throws Exception {
		// 추천하기 전 추천 수
		Answer beforeAnswer = answerService.getAnswer(1);
		Integer beforeSize = beforeAnswer.getVoters().size();

		// 추천하기
		ResultActions resultActions = mvc
			.perform(get("/answer/vote/1"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerVote"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/question/detail/*")); // 답변 있는 질문으로 리다이렉트

		// 중복 추천
		resultActions = mvc
			.perform(get("/answer/vote/1"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("answerVote"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/question/detail/*")); // 답변 있는 질문으로 리다이렉트

		// 추천 후 추천인 수
		Answer afterAnswer = answerService.getAnswer(1);
		Integer afterSize = afterAnswer.getVoters().size();

		// 1차이가 나는지 비교(Set으로 관리되기에 중복 추천이 안됨)
		assertThat(afterSize - 1).isEqualTo(beforeSize);
	}

	@Test
	@DisplayName("GET /answer/recent_list 는 최근 답변 15개 조회된다.")
	void t011() throws Exception {
		// 추천하기
		ResultActions resultActions = mvc
			.perform(get("/answer/recent_list"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(AnswerController.class))
			.andExpect(handler().methodName("showRecentList"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<H3>최근 답변</H3>
				      """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<h5 class="text-secondary">가장 최근에 달린 답변 15개까지 나타납니다.</h5>
				              """.stripIndent().trim())));
	}

}
