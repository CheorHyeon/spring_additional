package com.ll.spring_additional.boundedContext.question.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.question.service.QuestionService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class QuestionControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private QuestionService questionService;

	@Test
	@DisplayName("GET /question/list/{type} 는 게시판을 타입별로 선택하여 보여준다")
	void t001() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/question/list/qna"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("list"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<h3 class="border-bottom py-2">질문과답변</h3>
				 """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<a href="/question/create/qna" class="btn btn-primary">질문과답변 등록</a>
				""".stripIndent().trim())));

		// WHEN
		resultActions = mvc
			.perform(get("/question/list/free"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("list"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<h3 class="border-bottom py-2">자유게시판</h3>
				 """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<a href="/question/create/free" class="btn btn-primary">자유게시판 등록</a>
				""".stripIndent().trim())));

		// WHEN
		resultActions = mvc
			.perform(get("/question/list/bug"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("list"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<h3 class="border-bottom py-2">버그및건의</h3>
				 """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<a href="/question/create/bug" class="btn btn-primary">버그및건의 등록</a>
				""".stripIndent().trim())));
	}

	@Test
	@DisplayName("GET /question/detail/{id} 는 게시물 내용, 댓글 수, 답변 등을 보여준다.")
	void t002() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/question/detail/302"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("detail"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<h2 class="border-bottom py-2">질문입니닷22</h2>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<div class="card-text" style="white-space: pre-wrap;"><p>질문이에요!22</p>
				 """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<h5 class="border-bottom my-3 py-2">302개의 답변이 있습니다.</h5>
				 """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<div class="fw-bold" id="q-comment-count">7</div>
				     """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<div>개의 댓글이 있습니다.</div>
				    """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /question/create/{type} 는 게시판을 타입별로 게시글을 작성할 폼을 보여준다")
	void t003() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/question/create/qna"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("showCreate"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<h5 class="my-3 border-bottom pb-2">질문과답변 작성</h5>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<label for="subject" class="form-label">제목</label>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<input type="text" class="form-control" id="subject" name="subject" value="">
				 """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				     <label for="content" class="form-label">내용</label>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				     <textarea class="form-control" rows="10" id="content" name="content"></textarea>
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /question/create/{type} 는 게시판 타입별로 게시글을 작성하고, 해당 게시판 목록으로 리다이렉트")
	void t004() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/question/create/qna")
				.with(csrf()) // CSRF 키 생성
				.param("subject", "테스트 제목입니다잉")
				.param("content", "테스트 내용입니다잉")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionCreate"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/question/list/qna"));

		// WHEN
		resultActions = mvc
			.perform(post("/question/create/free")
				.with(csrf()) // CSRF 키 생성
				.param("subject", "테스트 제목입니다잉")
				.param("content", "테스트 내용입니다잉")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionCreate"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/question/list/free"));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /question/create/{type} 는 제목, 내용 중 하나라도 입력하지 않으면 실패")
	void t005() throws Exception {
		// 내용 등록 x
		ResultActions resultActions = mvc
			.perform(post("/question/create/qna")
				.with(csrf()) // CSRF 키 생성
				.param("subject", "테스트 제목입니다잉")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionCreate"))
			.andExpect(status().is4xxClientError());

		// 제목 등록 x
		resultActions = mvc
			.perform(post("/question/create/qna")
				.with(csrf()) // CSRF 키 생성
				.param("content", "테스트 내용입니다잉")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionCreate"))
			.andExpect(status().is4xxClientError());

		// 둘다 등록 x
		resultActions = mvc
			.perform(post("/question/create/qna")
				.with(csrf()) // CSRF 키 생성
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionCreate"))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /question/modify/{id} 는 게시글 수정 폼을 보여준다. 단 본인이 작성하지 않은 경우 예외 발생")
	void t006() throws Exception {
		// 본인이 작성하지 않은 게시글 수정폼 요청
		ResultActions resultActions = mvc
			.perform(get("/question/modify/258")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionModify"))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("수정권한이 없습니다."));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("GET /question/modify/{id} 는 본인이 작성한 글에 대해 수정 폼을 보여준다.")
	void t007() throws Exception {
		// 본인이 작성한 게시글 수정폼 요청
		ResultActions resultActions = mvc
			.perform(get("/question/modify/301")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionModify"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<label for="subject" class="form-label">제목</label>
				                  """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<input type="text" class="form-control" id="subject" name="subject" value="질문입니닷">
				                  """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<label for="content" class="form-label">내용</label>
				                  """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<textarea class="form-control" rows="10" id="content" name="content">질문이에요!</textarea>
				                  """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("POST /question/modify/{id} 는 본인이 작성한 글을 수정할 수 있으나, 수정할 내용과 제목 모두 입력해야 한다.")
	void t008() throws Exception {
		// 내용 등록 x
		ResultActions resultActions = mvc
			.perform(post("/question/modify/2")
				.with(csrf()) // CSRF 키 생성
				.param("subject", "테스트 제목입니다잉")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionModify"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<div>내용은 필수항목입니다.</div>
				              """.stripIndent().trim())));
		// 제목 등록 x
		resultActions = mvc
			.perform(post("/question/modify/2")
				.with(csrf()) // CSRF 키 생성
				.param("content", "테스트 내용입니다잉")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionModify"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<div>제목은 필수항목입니다.</div>
				              """.stripIndent().trim())));

		// 둘다 등록 x
		resultActions = mvc
			.perform(post("/question/modify/2")
				.with(csrf()) // CSRF 키 생성
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionModify"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<div>제목은 필수항목입니다.</div>
				              """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<div>내용은 필수항목입니다.</div>
				              """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("POST /question/modify/{id} 는 본인 게시글의 수정할 내용과 제목 모두 입력시 수정 성공한다")
	void t009() throws Exception {
		// 둘다 등록한 경우 : 성공하는 케이스
		ResultActions resultActions = mvc
			.perform(post("/question/modify/2")
				.with(csrf()) // CSRF 키 생성
				.param("content", "테스트 내용입니다잉")
				.param("subject", "테스트 제목입니다잉")

			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionModify"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/question/detail/2"));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("DELETE /question/{id} 는 본인이 작성한 게시글만 삭제할 수 있다.")
	void t010() throws Exception {
		// 내용 등록 x
		ResultActions resultActions = mvc
			.perform(delete("/question/2")
				.with(csrf()) // CSRF 키 생성
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionDelete"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("DELETE /question/{id} 는 본인이 작성하지 않은 게시글은 삭제할 수 없다.")
	void t011() throws Exception {
		// 내용 등록 x
		ResultActions resultActions = mvc
			.perform(delete("/question/2")
				.with(csrf()) // CSRF 키 생성
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionDelete"))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("삭제권한이 없습니다."));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /question/vote/{id} 는 게시글에 추천 기능을 적용한다. Set으로 관리되기에 중복 추천이 안됨")
	void t012() throws Exception {

		// 추천하기 전 추천 수
		Question question = questionService.getQuestion(302);
		Integer size = question.getVoters().size();

		// 추천하기
		ResultActions resultActions = mvc
			.perform(get("/question/vote/302"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionVote"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/question/detail/302"));

		resultActions = mvc
			.perform(get("/question/vote/302"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("questionVote"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/question/detail/302"));

		// 추천 후 추천인 수
		Question question2 = questionService.getQuestion(302);
		Integer size2 = question2.getVoters().size();

		// 1차이가 나는지 비교(Set으로 관리되기에 중복 추천이 안됨)
		assertThat(size2 - 1).isEqualTo(size);
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("GET /question/list/byQuestion/{id} 는 본인이 질문한 게시물 목록을 반환한다.")
	void t013() throws Exception {
		// 본인이 질문한 게시물 목록 반환
		ResultActions resultActions = mvc
			.perform(get("/question/list/byQuestion/3"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("personalListByQuestionUserId"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<p>user2님의</p>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<p>총 질문수</p>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<p>(302)</p>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				            <a href="/question/detail/298">테스트 데이터입니다:[298]</a>
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /question/list/byQuestion/{id} 는 본인이 질문한 게시물 목록을 반환하지만, 본인 id가 아닌 URI로 조회할 수 없다.")
	void t014() throws Exception {
		// 본인이 질문한 게시물 목록 반환
		ResultActions resultActions = mvc
			.perform(get("/question/list/byQuestion/3")) // user2의 id
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("personalListByQuestionUserId"))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("조회 권한이 없습니다."));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /question/list/byAnswer/{id} 는 본인이 답변한 질문 목록이 출력된다")
	void t015() throws Exception {
		// 본인이 답변한 게시물 목록 반환
		ResultActions resultActions = mvc
			.perform(get("/question/list/byAnswer/2"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("personalListByAnswerUserId"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("""
				<p>user1님의</p>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<p>총 답변수</p>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<p>(2)</p>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				            <a href="/question/detail/302">질문입니닷22</a>
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /question/list/byAnswer/{id} 는 본인이 질문한 게시물 목록을 반환하지만, 본인 id가 아닌 URI로 조회할 수 없다.")
	void t016() throws Exception {
		// 본인이 질문한 게시물 목록 반환
		ResultActions resultActions = mvc
			.perform(get("/question/list/byAnswer/3")) // user2의 id
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(QuestionController.class))
			.andExpect(handler().methodName("personalListByAnswerUserId"))
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("조회 권한이 없습니다."));
	}

}
