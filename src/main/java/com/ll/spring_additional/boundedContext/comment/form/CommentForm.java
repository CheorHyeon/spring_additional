package com.ll.spring_additional.boundedContext.comment.form;

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
	private Long questionId;
	private Long answerId;
	private Boolean secret = false;
	private Long parentId;
	private Long commentWriter;
}
