package com.ll.spring_additional.boundedContext.question.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.base.exception.DataNotFoundException;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;
import com.ll.spring_additional.boundedContext.question.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
	private final QuestionRepository questionRepository;

	public Page<Question> getList(int page, String kw) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); //페이지 번호, 개수
		return questionRepository.findAllByKeyword(kw, pageable);
	}

	public Question getQuestion(Integer id) {
		Optional<Question> question = questionRepository.findById(id);
		if (question.isPresent()) {
			return question.get();
		} else {
			throw new DataNotFoundException("question not found");
		}
	}

	@Transactional
	public Question updateQuestionView(Question question) {
		question.updateView();
		questionRepository.save(question);
		return question;
	}

	@Transactional
	public Question create(String subject, String content, SiteUser user) {
		Question q = new Question();
		q.setSubject(subject);
		q.setContent(content);
		q.setAuthor(user);
		questionRepository.save(q);
		return q;
	}

	@Transactional
	public void modify(Question question, String subject, String content) {
		question.setSubject(subject);
		question.setContent(content);
		questionRepository.save(question);
	}

	@Transactional
	public void delete(Question question) {
		questionRepository.delete(question);
	}

	@Transactional
	public void vote(Question question, SiteUser siteUser) {
		question.getVoters().add(siteUser);
		questionRepository.save(question);
	}

	public Long getQuestionCount(SiteUser author) {
		return questionRepository.countByAuthor(author);
	}

	public List<Question> getQuestionTop5LatestByUser(SiteUser author) {
		return questionRepository.findTop5ByAuthorOrderByCreateDateDesc(author);
	}

	// 유저 개인별 질문 모음(질문자)
	public Page<Question> getPersonalQuestionListByQuestionAuthorId(int page, String kw, Long authorId) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); //페이지 번호, 개수
		return questionRepository.findAllByKeywordAndAuthorId(kw, authorId, pageable);
	}

	// 유저 개인별 질문 모음(답변자)
	public Page<Question> getPersonalQuestionListByAnswer_AuthorId(int page, String kw, Long answerAuthorId) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); //페이지 번호, 개수
		return questionRepository.findAllByKeywordAndAnswer_AuthorId(kw, answerAuthorId, pageable);
	}
}
