package com.ll.spring_additional.boundedContext.comment.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.answer.service.AnswerService;
import com.ll.spring_additional.boundedContext.comment.entity.Comment;
import com.ll.spring_additional.boundedContext.comment.form.CommentForm;
import com.ll.spring_additional.boundedContext.comment.service.CommentService;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.question.service.QuestionService;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;
import com.ll.spring_additional.boundedContext.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
	private final CommentService commentService;
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserService userService;
	private final int PAGESIZE = 5;

	@GetMapping("/{type}/{id}")
	public String showComments(Model model, @ModelAttribute CommentForm commentForm,
		@RequestParam(defaultValue = "0") Integer commentPage, @PathVariable String type, @PathVariable Integer id, @RequestParam(required = false) Integer questionId) {

		Question question;
		if (type.equals("question")) {
			question= questionService.getQuestion(id);
			model.addAttribute("question", question);
			Page<Comment> commentPaging = commentService.getCommentPageByQuestion(commentPage, question);
			model.addAttribute("questionCommentPaging", commentPaging);
			model.addAttribute("totalCount", commentPaging.getTotalElements());
			return "comment/question_comment";
		}
		else {
			question= questionService.getQuestion(questionId);
			model.addAttribute("question", question);
			Answer answer = answerService.getAnswer(id);
			model.addAttribute("answer", answer);
			Page<Comment> commentPaging = commentService.getCommentPageByAnswer(commentPage, answer);
			model.addAttribute("answerCommentPaging", commentPaging);
			model.addAttribute("totalCount", commentPaging.getTotalElements());
			return "comment/answer_comment";
		}
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{type}")
	public String create(Model model, @ModelAttribute CommentForm commentForm, @PathVariable String type, Principal principal,
		BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return "question/question_detail";
		}

		SiteUser user = userService.getUser(principal.getName());
		String content = commentForm.getCommentContents().trim();
		Question question = questionService.getQuestion(commentForm.getQuestionId());

		int lastPage;

		if (type.equals("question")) {
			Comment comment = commentService.createByQuestion(content, commentForm.getSecret(), user, question);
			lastPage = commentService.getLastPageNumberByQuestion(question);
			Page<Comment> commentPaging = commentService.getCommentPageByQuestion(lastPage, question);
			model.addAttribute("questionCommentPaging", commentPaging);
			model.addAttribute("question", question);
			model.addAttribute("totalCount", commentPaging.getTotalElements());
			return "comment/question_comment :: #question-comment-list";
		} else {
			Answer answer = answerService.getAnswer(commentForm.getAnswerId());
			Comment comment = commentService.createByAnswer(content, commentForm.getSecret(), user, answer);
			lastPage = commentService.getLastPageNumberByAnswer(answer);
			Page<Comment> commentPaging = commentService.getCommentPageByAnswer(lastPage, answer);
			model.addAttribute("answerCommentPaging", commentPaging);
			model.addAttribute("question", question);
			model.addAttribute("answer", answer);
			model.addAttribute("totalCount", commentPaging.getTotalElements());
			return "comment/answer_comment :: #answer-comment-list";
		}
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/reply/create/{type}")
	public String replyCreate(Model model, @ModelAttribute CommentForm commentForm, @PathVariable String type, BindingResult bindingResult,
		Principal principal) {

		if (bindingResult.hasErrors()) {
			return "question/question_detail";
		}

		Question question = questionService.getQuestion(commentForm.getQuestionId());
		SiteUser user = userService.getUser(principal.getName());

		// 부모 댓글 찾아오기
		Comment parent = commentService.getComment(commentForm.getParentId());

		if (parent == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 댓글을 찾을 수 없습니다");
		}

		model.addAttribute("question", question);

		int page=0;
		Page<Comment> paging;

		// 자식 댓글 생성
		if (type.equals("question")) {
			Comment comment = commentService.createReplyCommentByQuestion(commentForm.getCommentContents(),
				commentForm.getSecret(), user, question, parent);
			// 부모 댓글이 있는 페이지로 가야하므로, 부모의 페이지를 구해옴
			page = commentService.getPageNumberByQuestion(question, comment, PAGESIZE);
			paging = commentService.getCommentPageByQuestion(page, question);
			model.addAttribute("questionCommentPaging", paging);
			// 전체 댓글 수 갱신
			model.addAttribute("totalCount", paging.getTotalElements());
			return "comment/question_comment :: #question-comment-list";
		} else {
			Answer answer = answerService.getAnswer(commentForm.getAnswerId());
			model.addAttribute("answer", answer);
			Comment comment = commentService.createReplyCommentByAnswer(commentForm.getCommentContents(),
				commentForm.getSecret(), user, answer, parent);
			// 부모 댓글이 있는 페이지로 가야하므로, 부모의 페이지를 구해옴
			page = commentService.getPageNumberByAnswer(answer, comment, PAGESIZE);
			paging = commentService.getCommentPageByAnswer(page, answer);
			model.addAttribute("answerCommentPaging", paging);
			// 전체 댓글 수 갱신
			model.addAttribute("totalCount", paging.getTotalElements());
			return "comment/answer_comment :: #answer-comment-list";
		}
	}

	// 답글 수정 + 댓글 수정 둘다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{type}")
	public String modify(CommentForm commentForm, Model model, Principal principal, @PathVariable String type) {
		Question question = questionService.getQuestion(commentForm.getQuestionId());
		SiteUser user = userService.getUser(principal.getName());

		if (!(user.isAdmin()) && (user.getId() != commentForm.getCommentWriter())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
		}

		// 대댓글(답글)도 id로 찾을 수 있음(댓글과 동일 객체 사용이 가능하니 하나의 메서드로 처리 가능)
		Comment comment = commentService.getComment(commentForm.getId());

		// 댓글 내용, 비밀 댓글 여부만 수정 할테니 해당 값 넘기기
		commentService.modify(comment, commentForm.getCommentContents().trim(), commentForm.getSecret());

		model.addAttribute("question", question);

		Page<Comment> paging;
		int page=0;

		if (type.equals("question")) {
			page = commentService.getPageNumberByQuestion(question, comment, PAGESIZE);
			paging = commentService.getCommentPageByQuestion(page, question);
			model.addAttribute("questionCommentPaging", paging);
			return "comment/question_comment :: #question-comment-list";
		} else {
			Answer answer = answerService.getAnswer(commentForm.getAnswerId());
			model.addAttribute("answer", answer);
			page = commentService.getPageNumberByAnswer(answer, comment, PAGESIZE);
			paging = commentService.getCommentPageByAnswer(page, answer);
			model.addAttribute("answerCommentPaging", paging);
			return "comment/answer_comment :: #answer-comment-list";
		}
	}

	// 댓글 삭제 메서드
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete/{type}")
	public String delete(Model model, CommentForm commentForm, Principal principal, @PathVariable String type) {
		Question question = questionService.getQuestion(commentForm.getQuestionId());
		SiteUser user = userService.getUser(principal.getName());

		// 관리자가 아니거나 현재 로그인한 사용자가 작성한 댓글이 아니면 삭제 불가
		if (!(user.isAdmin()) && (user.getId() != commentForm.getCommentWriter())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
		}

		Comment comment = commentService.getComment(commentForm.getId());


		// 댓글이 속한 페이지 번호
		int page = 0;

		Answer answer = null;
		if (type.equals("question")) {
			page = commentService.getPageNumberByQuestion(question, comment, PAGESIZE);
		}

		else {
			answer = answerService.getAnswer(commentForm.getAnswerId());
			page = commentService.getPageNumberByAnswer(answer, comment, PAGESIZE);
		}

		// 부모(댓글)이 있을 경우 연관관계 끊어주기 -> 삭제되더라도 GET 등으로 새로 요청을 보내는 것이 아니기에
		// 이 작업은 꼭 해줘야 대댓글 리스트도 수정된다!

		// 부모댓글이 삭제 되지 않았다면 연관관계 끊어주기만 하면 됨
		// => Ajax 비동기 리스트화를 위해 리스트에서 명시적 삭제
		if (comment.getParent() != null && !comment.getParent().isDeleted()) {
			comment.getParent().getChildren().remove(comment);
		}
		// 부모댓글이 삭제 상태이고 부모의 자식 댓글이 본인 포함 2개 이상이라면
		// 자식 댓글의 삭제가 부모 댓글 객체 삭제에 영향을 주지 않으니 연관관계만 끊어주기
		// => Ajax 비동기 리스트화를 위해 리스트에서 명시적 삭제
		else if (comment.getParent() != null && comment.getParent().isDeleted()
			&& comment.getParent().getChildren().size() > 1) {
			comment.getParent().getChildren().remove(comment);
		}

		commentService.delete(comment);

		model.addAttribute("question", question);
		Page<Comment> paging;

		if (type.equals("question")) {
			paging = commentService.getCommentPageByQuestion(page, question);
			// 만일 삭제 전이 6개 -> 삭제하면 5개 -> 0패이지가 보여져야 하는데, 삭제 전에 page를 계산하여 1페이지가 보여짐.
			// 여기서 조건을 검사해줘야 함, 현재 페이지 개수가 0개면 이전 페이지 이동, 단 현재 페이지가 0인데도 개수가 0개?
			// 그러면 댓글이 아에 없으니 그냥 0페이지 표기!
			// 삭제하기 전에 page를 구한 이유는 댓글이 삭제되면 삭제한 댓글이 원래 어디 페이지에 있는지 검사가 안되기 때문
			if(page !=0 && paging.getNumberOfElements() == 0)
				paging = commentService.getCommentPageByQuestion(page-1, question);
			model.addAttribute("questionCommentPaging", paging);
			// 전체 댓글 수 갱신
			model.addAttribute("totalCount", paging.getTotalElements());
			return "comment/question_comment :: #question-comment-list";
		} else {
			paging = commentService.getCommentPageByAnswer(page, answer);
			if((page !=0 && paging.getNumberOfElements() == 0))
				paging = commentService.getCommentPageByAnswer(page-1, answer);

			model.addAttribute("answer", answer);
			model.addAttribute("answerCommentPaging", paging);
			// 전체 댓글 수 갱신
			model.addAttribute("totalCount", paging.getTotalElements());
			return "comment/answer_comment :: #answer-comment-list";
		}
	}
}
