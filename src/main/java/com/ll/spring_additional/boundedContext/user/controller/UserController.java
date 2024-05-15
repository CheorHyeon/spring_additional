package com.ll.spring_additional.boundedContext.user.controller;

import java.net.http.HttpRequest;
import java.security.Principal;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ll.spring_additional.base.exception.DataNotFoundException;
import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.answer.service.AnswerService;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.question.service.QuestionService;
import com.ll.spring_additional.boundedContext.user.Form.PWChangeForm;
import com.ll.spring_additional.boundedContext.user.Form.UserCreateForm;
import com.ll.spring_additional.boundedContext.user.Form.UserPWFindForm;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;
import com.ll.spring_additional.boundedContext.user.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

	private final QuestionService questionService;

	private final AnswerService answerService;

	private final PasswordEncoder passwordEncoder;

	@GetMapping("/login")
	public String login() {
		return "user/login_form";
	}

	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "user/signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, HttpServletRequest req) {
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
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "user/signup_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "user/signup_form";
		}

		// 가입 후 자동로그인 처리
		try {
			req.login(userCreateForm.getUsername(), userCreateForm.getPassword1());
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}

		return "redirect:/";
	}

	@GetMapping("/mypage")
	@PreAuthorize("isAuthenticated()")
	public String showmyPage(Model model, Principal principal) {
		SiteUser user = userService.getUser(principal.getName());

		if (user == null) {
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

	@PreAuthorize("isAnonymous()")
	@GetMapping("/pw_find")
	public String showFindPassWord(UserPWFindForm userPWFindForm) {
		return "user/pw_find";
	}

	@PreAuthorize("isAnonymous()")
	@PostMapping("/pw_find")
	public String findPassWord(@Valid UserPWFindForm userPWFindForm, BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "user/pw_find";
		}

		SiteUser user = userService.getUser(userPWFindForm.getUsername());

		if (user == null) {
			bindingResult.reject("notFindUser", "일치하는 사용자가 없습니다.");
			return "user/pw_find";
		}

		if (!user.getEmail().equals(userPWFindForm.getEmail())) {
			bindingResult.reject("notCorrectEmail", "등록된 회원 정보와 이메일이 다릅니다.");
			return "user/pw_find";
		}

		String tempPW = userService.setTemporaryPW(user);

		// 이메일 전송
		userService.sendEmail(userPWFindForm.getEmail(), user.getUsername(), tempPW);

		// 로그인 페이지에서 보여줄 성공 메시지를 플래시 애트리뷰트로 추가
		redirectAttributes.addFlashAttribute("successMessage", "임시 비밀번호가 이메일로 전송되었습니다. 이메일 확인 후 로그인 해주세요.");

		return "redirect:/user/login";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/change/passwd")
	public String showChangePW(@ModelAttribute("pwChangeForm") PWChangeForm pwChangeForm) {
		return "user/pw_change";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/change/passwd")
	public String changePW(@Valid @ModelAttribute("pwChangeForm") PWChangeForm pwChangeForm,
		BindingResult bindingResult, Model model,
		Principal principal, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "user/pw_change";
		}

		SiteUser user = userService.getUser(principal.getName());

		// 이전 패스워드와 맞지 않을경우
		if (!passwordEncoder.matches(pwChangeForm.getPrePassword(), user.getPassword())) {
			bindingResult.reject("notMatchPW", "이전 비밀번호가 일치하지 않습니다.");
			return "user/pw_change";
		}
		// 새 비밀번호, 비밀번호 확인 창 일치하지 않을경우
		if (!pwChangeForm.getNewPassword1().equals(pwChangeForm.getNewPassword2())) {
			bindingResult.reject("notMatchNewPW", "새 비밀번호와 확인이 일치하지 않습니다.");
			return "user/pw_change";
		}

		userService.updatePassWord(user, pwChangeForm.getNewPassword1());

		// 로그인 페이지에서 보여줄 성공 메시지를 플래시 애트리뷰트로 추가
		redirectAttributes.addFlashAttribute("successMessage", "비밀번호 변경 성공!");

		return "redirect:/user/mypage";
	}
}