package com.ll.spring_additional.boundedContext.question.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
	@NotBlank(message="제목은 필수항목입니다.")
	@Size(max=200, message = "200자를 넘을 수 없습니다.")
	private String subject;

	@NotBlank(message="내용은 필수항목입니다.")
	private String content;
}