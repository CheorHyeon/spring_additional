package com.ll.spring_additional.boundedContext.question.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.ll.spring_additional.base.exception.DataNotFoundException;
import com.ll.spring_additional.boundedContext.answer.form.AnswerForm;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.question.form.QuestionForm;
import com.ll.spring_additional.boundedContext.question.service.QuestionService;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;
import com.ll.spring_additional.boundedContext.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
	private final QuestionService questionService;
	private final UserService userService;

	@GetMapping("/list")
	public String list(Model model, @RequestParam(value="page", defaultValue="0") int page
		, @RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<Question> paging = questionService.getList(page, kw);
		model.addAttribute("paging", paging);
		return "question/question_list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list/{id}")
	public String list(Model model, @PathVariable Long id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue = "") String kw,
		Principal principal) {

		SiteUser siteUser = userService.getUser(principal.getName());

		if(siteUser.getId() != id) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조회 권한이 없습니다.");
		}

		Page<Question> paging = questionService.getPersonalList(page, kw, id);
		model.addAttribute("user", siteUser);
		model.addAttribute("paging", paging);
		return "question/personal_list";
	}
	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable Integer id, AnswerForm answerForm) {
		Question question = questionService.getQuestion(id);
		model.addAttribute("question", question);
		return "question/question_detail";
	}

	@GetMapping("/increase")
	@ResponseBody
	public String increaseHit(Integer questionId, @RequestParam(required = false) Boolean isVisited) {
		Question question = questionService.getQuestion(questionId);

		// 방문한 적이 없을때만 조회수 증가
		if (isVisited != null && !isVisited) {
			questionService.updateQuestionView(question);
		}

		return Integer.toString(question.getView());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String questionCreate(QuestionForm questionForm){
		return "question/question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "question/question_form";
		}
		SiteUser siteUser = userService.getUser(principal.getName());
		questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
		return "redirect:/question/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
		Question question = questionService.getQuestion(id);
		if(!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());
		return "question/question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
		Principal principal, @PathVariable("id") Integer id) {
		if (bindingResult.hasErrors()) {
			return "question/question_form";
		}
		Question question = questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
		return String.format("redirect:/question/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String questionDelete(Principal principal, @PathVariable Integer id) {
		Question question = questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		questionService.delete(question);
		return "redirect:/";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String questionVote(Principal principal, @PathVariable("id") Integer id) {
		Question question = questionService.getQuestion(id);
		SiteUser siteUser = userService.getUser(principal.getName());
		questionService.vote(question, siteUser);
		return String.format("redirect:/question/detail/%s", id);
	}
}