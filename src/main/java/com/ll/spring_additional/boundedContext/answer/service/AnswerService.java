package com.ll.spring_additional.boundedContext.answer.service;

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
import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.answer.repository.AnswerRepository;
import com.ll.spring_additional.boundedContext.answerVoter.AnswerVoter;
import com.ll.spring_additional.boundedContext.answerVoter.AnswerVoterRepository;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AnswerService {

	private final AnswerRepository answerRepository;

	private final AnswerVoterRepository answerVoterRepository;

	@Transactional
	public Answer create(Question question, String content, SiteUser author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setQuestion(question);
		answer.setAuthor(author);
		answerRepository.save(answer);
		return answer;
	}

	public Answer getAnswer(Long id) {
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
		AnswerVoter answerVoter = answerVoterRepository.findByAnswerAndVoter(answer, siteUser);

		// 이미 추천했다면 삭제
		if(answerVoter != null) {
			// answer에 Set 갱신
			answer.getVoters().remove(answerVoter);
			// 연관관계 주인도 업데이트
			answerVoterRepository.delete(answerVoter);
			return;
		}

		// 추천 안했다면 새로 추천 처리
		AnswerVoter newAnswerVote = new AnswerVoter();
		newAnswerVote.setAnswer(answer);
		newAnswerVote.setVoter(siteUser);
		AnswerVoter saveAnswerVote = answerVoterRepository.save(newAnswerVote);
		answer.getVoters().add(saveAnswerVote);
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

	public Page<Answer> getAnswerPage(Question question, int page, String sort) {
		Pageable pageable;

		// 최신순
		if (sort.equals("createDate")) {
			List<Sort.Order> sorts = new ArrayList<>();
			sorts.add(Sort.Order.desc("createDate"));
			pageable = PageRequest.of(page, 10, Sort.by(sorts)); //페이지 번호, 개수
			return answerRepository.findAllByQuestion(question, pageable);
		}

		// 추천순, 기본
		else {
			pageable = PageRequest.of(page, 10); // 페이지네이션 정보
			// 추천순 : 10개에 페이지정보만 주면 알아서
			if (sort.equals("voter"))
				return answerRepository.findAllByQuestionOrderByVoter(question, pageable);
			// 기본
			return answerRepository.findAllByQuestion(question, pageable);
		}
	}
}