package com.ll.spring_additional.answer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ll.spring_additional.question.Question;
import com.ll.spring_additional.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(columnDefinition = "TEXT")
	private String content;

	private LocalDateTime createDate;

	private LocalDateTime modifyDate;

	@ManyToOne // 부모 : question
	private Question question;

	@ManyToOne
	private SiteUser author;

	@ManyToMany
	private Set<SiteUser> voters = new LinkedHashSet<>();
}