package com.ll.spring_additional.boundedContext.user.controller;
import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ll.spring_additional.base.exception.DataNotFoundException;
import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.answer.service.AnswerService;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.question.service.QuestionService;
import com.ll.spring_additional.boundedContext.user.Form.UserCreateForm;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;
import com.ll.spring_additional.boundedContext.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	private final QuestionService questionService;

	private final AnswerService answerService;

	@GetMapping("/login")
	public String login() {
		return "user/login_form";
	}

	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "user/signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user/signup_form";
		}

		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect",
				"2개의 패스워드가 일치하지 않습니다.");
			return "user/signup_form";
		}

		try {
			userService.create(userCreateForm.getUsername(),
				userCreateForm.getEmail(), userCreateForm.getPassword1());
		}catch(DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "user/signup_form";
		}catch(Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "user/signup_form";
		}
		return "redirect:/";
	}

	@GetMapping("/mypage")
	@PreAuthorize("isAuthenticated()")
	public String showmyPage(Model model, Principal principal)
	{
		SiteUser user = userService.getUser(principal.getName());

		if(user == null) {
			throw new DataNotFoundException("사용자를 찾을 수 없습니다.");
		}
		model.addAttribute("user", user);

		Long questionCount = questionService.getQuestionCount(user);
		model.addAttribute("questionCount", questionCount);

		List<Question> questionList = questionService.getQuestionTop5LatestByUser(user);
		model.addAttribute("questionList", questionList);

		Long answerCount = answerService.getAnswerCount(user);
		model.addAttribute("answerCount", answerCount);

		List<Answer> answerList = answerService.getAnswerTop5LatestByUser(user);
		model.addAttribute("answerList", answerList);

		return "user/my_page";
	}
}