package com.ll.spring_additional.boundedContext.answer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.spring_additional.boundedContext.answerVoter.AnswerVoter;
import com.ll.spring_additional.boundedContext.comment.entity.Comment;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class) // + enableJpaAuditing => JPA Auditing 활성
// JPA Auditing : 시간에 대해 자동으로 값을 넣어주는 기능
public class Answer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "TEXT")
	private String content;

	@CreatedDate
	private LocalDateTime createDate;

	@LastModifiedDate
	private LocalDateTime modifyDate;

	@PrePersist
	public void prePersist() {
		this.modifyDate = null; // 객체 생성 시 처음에 수정일 null값으로 설정
	}


	@ManyToOne // 부모 : question
	private Question question;

	@ManyToOne
	private SiteUser author;

	@OneToMany(fetch = FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	private Set<AnswerVoter> voters = new HashSet<>();

	@OneToMany(mappedBy = "answer", cascade = {CascadeType.REMOVE})
	@ToString.Exclude
	@LazyCollection(LazyCollectionOption.EXTRA) // commentList.size(); 함수가 실행될 때 SELECT COUNT 실행
	private List<Comment> comments = new ArrayList<>();
}