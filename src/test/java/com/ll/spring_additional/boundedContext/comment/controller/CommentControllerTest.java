package com.ll.spring_additional.boundedContext.comment.controller;

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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {
	@Autowired
	private MockMvc mvc;

	@Test
	@WithUserDetails("user1")
	@DisplayName("GET /comment/{type}/{id}는 질문/답변을 구분하여 댓글을 조회한다.")
	void t001() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/comment/question/302"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("showComments"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<div id="new_total_question_comments" style="display: none;">7</div>
				                """.stripIndent().trim()))) //추후 댓글 추가로 생길 시 개수 갱신용, 현재는 7개
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">테스트 댓글1</p>
				          """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">테스트 댓글1</p>
						""".stripIndent().trim())));

		// WHEN
		resultActions = mvc
			.perform(get("/comment/answer/1")
				.param("questionId", "302"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("showComments"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<div id="new_total_answer_comments" style="display: none;">5</div>
				                  """.stripIndent().trim()))) // 추후 댓글 추가로 생길 시 개수 갱신용 현재는 5개
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">테스트 댓글</p>
				          """.stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">테스트 대댓글</p>
				          """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /comment/create/{type}는 질문/답변을 구분하여 댓글을 작성한다.")
	void t002() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/comment/create/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("secret", "false")
				.param("commentContents", "이거 질문에 댓글 달린다!")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("create"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">이거 질문에 댓글 달린다!</p>
				          """.stripIndent().trim())));

		resultActions = mvc
			.perform(post("/comment/create/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("secret", "false")
				.param("answerId", "2")
				.param("commentContents", "이거 답변에 댓글 달린다!")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("create"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">이거 답변에 댓글 달린다!</p>
				          """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user1")
	@DisplayName("POST /comment/reply/create/{type}는 질문/답변을 구분하여 대댓글을 작성한다.")
	void t003() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/comment/reply/create/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("secret", "false")
				.param("commentContents", "이거 질문에 달린 대댓글!")
				.param("parentId", "1")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("replyCreate"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">이거 질문에 달린 대댓글!</p>
				          """.stripIndent().trim())));

		resultActions = mvc
			.perform(post("/comment/reply/create/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("secret", "false")
				.param("answerId", "1")
				.param("parentId", "8")
				.param("commentContents", "이거 답변에 달린 대댓글!")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("replyCreate"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">이거 답변에 달린 대댓글!</p>
				          """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("POST /comment/modify/{type}는 질문에 달린 댓글/대댓글을 수정한다.")
	void t004_1() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/comment/modify/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("secret", "false")
				.param("commentContents", "질문에 달린 댓글 수정 성공")
				.param("id", "1")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">질문에 달린 댓글 수정 성공</p>
				          """.stripIndent().trim())));

		resultActions = mvc
			.perform(post("/comment/modify/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("secret", "false")
				.param("commentContents", "질문에 달린 대댓글 수정 성공")
				.param("id", "1")
				.param("parentId", "1")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">질문에 달린 대댓글 수정 성공</p>
				          """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("user2")
	@DisplayName("POST /comment/modify/{type}는 답변에 달린 댓글/대댓글도 수정한다.")
	void t004_2() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/comment/modify/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("answerId", "1")
				.param("commentContents", "답변에 달린 댓글 수정 성공")
				.param("id", "8")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">답변에 달린 댓글 수정 성공</p>
				          """.stripIndent().trim())));

		resultActions = mvc
			.perform(post("/comment/modify/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("answerId", "1")
				.param("commentContents", "답변에 달린 대댓글 수정 성공")
				.param("id", "9")
				.param("parentId", "8")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p style="white-space: pre-wrap;">답변에 달린 대댓글 수정 성공</p>
				          """.stripIndent().trim())));
	}

	/*
		댓글 삭제
		- 삭제 하려는 댓글의 대댓글이 있을 경우 : 내용 안보이도록 ("삭제된 댓글입니다" 출력)
		- 삭제 하려는 댓글의 부모 댓글이 있을경우 : 대댓글 자체 삭제
		- 삭제 하려는 댓글의 자식 댓글이 없는 경우 : 댓글 자체 삭제
		- 부모 댓글 삭제인 상황에서 자식 댓글 삭제 : 부모 댓글 객체도 같이 삭제됨
	 */
	@Test
	@WithUserDetails("user2")
	@DisplayName("POST /comment/delete/{type}는 질문에 달린 댓글/대댓글을 삭제한다.")
	void t005() throws Exception {
		// 삭제 하려는 댓글의 대댓글이 있을 경우 : 내용 안보이도록
		ResultActions resultActions = mvc
			.perform(post("/comment/delete/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("id", "1")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p class="text-body-tertiary"> 삭제된 댓글입니다.</p>
				          """.stripIndent().trim())));

		// 삭제 하려는 댓글의 부모 댓글이 있을경우 : 대댓글 자체 삭제
		resultActions = mvc
			.perform(post("/comment/delete/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("id", "7")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(not(containsString("""
				 테스트 대댓글
				          """.stripIndent().trim()))));

		// 삭제 하려는 댓글의 자식 댓글이 없을경우 : 댓글 자체 삭제
		resultActions = mvc
			.perform(post("/comment/delete/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("id", "2") // 내용 : 테스트 댓글2
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(not(containsString("""
				<p style="white-space: pre-wrap;">테스트 댓글2</p>
				          """.stripIndent().trim()))));

		// 부모 댓글 삭제인 상황에서 자식 댓글 삭제 : 부모 댓글 객체도 같이 삭제됨
		resultActions = mvc
			.perform(post("/comment/delete/question")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "301")
				.param("id", "11") // 내용 : (질문)부모 삭제된 테스트용 자식 데이터, 이녀석 삭제시 부모도 사라짐
				.param("commentWriter", "3")
			)
			.andDo(print());

		// 둘다 삭제되어 존재하지 않음
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(not(containsString("""
				"삭제된 댓글입니다"
				        """.stripIndent().trim()))))
			.andExpect(content().string(not(containsString("""
				(질문)부모 삭제된 테스트용 부모 데이터, 자식 삭제시 이녀석도 사라짐
				          """.stripIndent().trim()))))
			.andExpect(content().string(not(containsString("""
				(질문)부모 삭제된 테스트용 자식 데이터, 이녀석 삭제시 부모도 사라짐
				          """.stripIndent().trim()))));
	}
	@Test
	@WithUserDetails("user2")
	@DisplayName("POST /comment/delete/{type}는 답변에 달린 댓글/대댓글도 삭제한다.")
	void t006() throws Exception {
		// 삭제 하려는 댓글의 대댓글이 있을 경우 : 내용 안보이도록
		ResultActions resultActions = mvc
			.perform(post("/comment/delete/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("answerId", "1")
				.param("id", "8")  // 8번(부모) : "테스트 댓글" - 9번(대댓글) : "테스트 대댓글"
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<p class="text-body-tertiary"> 삭제된 댓글입니다.</p>
				          """.stripIndent().trim())));

		// 삭제 하려는 댓글의 부모 댓글이 있을경우 : 대댓글 자체 삭제
		resultActions = mvc
			.perform(post("/comment/delete/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("answerId", "1")
				.param("id", "9")  // 8번(부모) : "테스트 댓글" - 9번(대댓글) : "테스트 대댓글"
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(not(containsString("""
				 테스트 대댓글
				          """.stripIndent().trim()))));

		// 삭제 하려는 댓글의 자식 댓글이 없을경우 : 댓글 자체 삭제
		resultActions = mvc
			.perform(post("/comment/delete/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("answerId", "1")
				.param("id", "14")  // 내용 : "대댓글 없는 댓글"
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(not(containsString("""
				대댓글 없는 댓글
				          """.stripIndent().trim()))));

		// 부모 댓글 삭제인 상황에서 자식 댓글 삭제 : 부모 댓글 객체도 같이 삭제됨
		resultActions = mvc
			.perform(post("/comment/delete/answer")
				.with(csrf()) // CSRF 키 생성
				.param("questionId", "302")
				.param("answerId", "1")
				.param("id", "13") // 내용 : (응답)부모 삭제된 테스트용 자식 데이터, 이녀석 삭제시 부모도 사라짐
				.param("commentWriter", "3")
			)
			.andDo(print());

		// 둘다 삭제되어 존재하지 않음
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(not(containsString("""
				"삭제된 댓글입니다"
				        """.stripIndent().trim()))))
			.andExpect(content().string(not(containsString("""
				(응답)부모 삭제된 테스트용 부모 데이터, 자식 삭제시 이녀석도 사라짐
				          """.stripIndent().trim()))))
			.andExpect(content().string(not(containsString("""
				(응답)부모 삭제된 테스트용 자식 데이터, 이녀석 삭제시 부모도 사라짐
				          """.stripIndent().trim()))));

	}
}
