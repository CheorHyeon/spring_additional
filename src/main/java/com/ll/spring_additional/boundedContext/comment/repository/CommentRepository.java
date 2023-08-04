package com.ll.spring_additional.boundedContext.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.spring_additional.boundedContext.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}