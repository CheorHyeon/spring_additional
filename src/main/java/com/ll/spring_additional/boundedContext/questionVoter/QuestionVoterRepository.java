package com.ll.spring_additional.boundedContext.questionVoter;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

public interface QuestionVoterRepository extends JpaRepository<QuestionVoter, Long> {
	QuestionVoter findByQuestionAndVoter(Question question, SiteUser siteUser);
}
