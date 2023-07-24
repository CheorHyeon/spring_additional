package com.ll.spring_additional.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.spring_additional.answer.Answer;
import com.ll.spring_additional.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class) // + enableJpaAuditing => JPA Auditing 활성
// JPA Auditing : 시간에 대해 자동으로 값을 넣어주는 기능
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 200)
	private String subject;

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

	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	@LazyCollection(LazyCollectionOption.EXTRA) // answerList.size(); 함수가 실행될 때 SELECT COUNT 실행
	// N+1 문제는 발생하지만, 한 페이지에 보여주는 10개의 게시물의 정보를 가져와서 개수를 표기하는 것 보다는 덜 부담
	private List<Answer> answerList = new ArrayList<>();

	@ManyToOne
	private SiteUser author;

	@ManyToMany
	private Set<SiteUser> voters = new LinkedHashSet<>();
}
