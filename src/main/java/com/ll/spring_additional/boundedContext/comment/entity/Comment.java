package com.ll.spring_additional.boundedContext.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ll.spring_additional.boundedContext.answer.entity.Answer;
import com.ll.spring_additional.boundedContext.question.entity.Question;
import com.ll.spring_additional.boundedContext.user.entity.SiteUser;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private SiteUser writer;

	@ManyToOne
	private Question question;

	@ManyToOne
	private Answer answer;

	@ToString.Exclude
	@ManyToOne
	private Comment parent;

	// Cascade REMOVE 불가 : 자식 댓글이 있는 상태에서, 그냥 댓글 삭제하면 자식 댓글 전부 지워짐
	// OrphanRemoval로 대댓글과 연관관계 끊어지면 삭제되게 설정
	@OneToMany(mappedBy = "parent", orphanRemoval = true)
	@ToString.Exclude
	@Builder.Default // 빌더패턴 리스트시 초기화
	private List<Comment> children = new ArrayList<>();

	private String content;

	@CreatedDate
	private LocalDateTime createDate;
	@LastModifiedDate
	private LocalDateTime modifyDate;

	@Builder.Default
	private Boolean secret = false;

	// 삭제 여부 나타내는 속성 추가
	@Builder.Default
	private Boolean deleted = false;
	@PrePersist
	public void prePersist() {
		this.modifyDate = null;
	}

	public void deleteParent() {
		deleted = true;
	}

	// 타임리프에서 비밀 댓글이면 댓글의 내용이 안보이게 하기 위함
	public boolean isSecret() {
		return this.secret == true;
	}
	// 타임리프에서 삭제 댓글이면 댓글의 내용이 안보이게 하기 위함
	public boolean isDeleted() {
		return this.deleted == true;
	}

}
