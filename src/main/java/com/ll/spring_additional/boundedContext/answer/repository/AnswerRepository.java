package com.ll.spring_additional.boundedContext.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

}