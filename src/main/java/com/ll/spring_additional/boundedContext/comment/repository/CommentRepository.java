package com.ll.spring_additional.boundedContext.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.comment.entity.Comment;
import com.ll.spring_additional.boundedContext.question.entity.Question;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	int countByQuestion(Question question);

	int countByAnswer(Answer answer);

	Page<Comment> findAllByQuestion(Question question, Pageable pageable);

	Page<Comment> findAllByAnswer(Answer answer, Pageable pageable);
}
