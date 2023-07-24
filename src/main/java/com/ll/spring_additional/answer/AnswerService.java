package com.ll.spring_additional.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.spring_additional.DataNotFoundException;
import com.ll.spring_additional.question.Question;
import com.ll.spring_additional.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AnswerService {

	private final AnswerRepository answerRepository;


	@Transactional
	public Answer  create(Question question, String content, SiteUser author) {
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
}