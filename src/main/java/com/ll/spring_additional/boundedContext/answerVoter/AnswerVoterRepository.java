package com.ll.spring_additional.boundedContext.answerVoter;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

public interface AnswerVoterRepository extends JpaRepository<AnswerVoter, Long> {

	AnswerVoter findByAnswerAndVoter(Answer answer, SiteUser siteUser);
}
