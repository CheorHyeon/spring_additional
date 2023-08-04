package com.ll.spring_additional.boundedContext.answer.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

	Long countByAuthor(SiteUser author);

	List<Answer> findTop5ByAuthorOrderByCreateDateDesc(SiteUser user);

	List<Answer> findTop15ByOrderByCreateDateDesc();

	Page<Answer> findAllByQuestion(Question question, Pageable pageable);

	@Query("SELECT e "
		+ "FROM Answer e "
		+ "WHERE e.question = :question "
		+ "ORDER BY SIZE(e.voters) DESC, e.createDate")
	Page<Answer> findAllByQuestionOrderByVoter(@Param("question") Question question, Pageable pageable);
}