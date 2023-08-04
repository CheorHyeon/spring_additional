package com.ll.spring_additional.boundedContext.answer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.base.exception.DataNotFoundException;
import com.ll.spring_additional.boundedContext.answer.repository.AnswerRepository;
import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AnswerService {

	private final AnswerRepository answerRepository;


	@Transactional
	public Answer create(Question question, String content, SiteUser author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setQuestion(question);
		answer.setAuthor(author);
		answerRepository.save(answer);
		return answer;
	}

	public Answer getAnswer(Integer id) {
		Optional<Answer> answer = answerRepository.findById(id);
		if (answer.isPresent()) {
			return answer.get();
		} else {
			throw new DataNotFoundException("answer not found");
		}
	}

	@Transactional
	public void modify(Answer answer, String content) {
		answer.setContent(content);
		answerRepository.save(answer);
	}

	@Transactional
	public void delete(Answer answer) {
		answerRepository.delete(answer);
	}

	@Transactional
	public void vote(Answer answer, SiteUser siteUser) {
		answer.getVoters().add(siteUser);
		answerRepository.save(answer);
	}

	public Long getAnswerCount(SiteUser author) {
		return answerRepository.countByAuthor(author);
	}

	public List<Answer> getAnswerTop5LatestByUser(SiteUser user) {
		return answerRepository.findTop5ByAuthorOrderByCreateDateDesc(user);
	}

	public List<Answer> getAnswerTop15Latest() {
		return answerRepository.findTop15ByOrderByCreateDateDesc();
	}

	public Page<Answer> getAnswerPage(Question question, int page) {
		Pageable pageable = PageRequest.of(page, 10); // 페이지네이션 정보
		return answerRepository.findAllByQuestion(question, pageable);

	}
}