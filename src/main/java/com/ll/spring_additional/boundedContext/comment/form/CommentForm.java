package com.ll.spring_additional.boundedContext.comment.form;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentForm {
	private Long id;
	@NotBlank(message = "내용은 필수항목입니다.")
	private String commentContents;
	private Integer questionId;
	private Integer answerId;
	private Boolean secret = false;
	private Long parentId;
	private Long commentWriter;
}
