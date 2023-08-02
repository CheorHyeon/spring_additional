package com.ll.spring_additional.boundedContext.user.Form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PWChangeForm {
	@NotBlank(message = "기존 비밀번호는 필수항목입니다.")
	private String prePassword;

	@NotBlank(message = "새 비밀번호는 필수항목입니다.")
	private String newPassword1;

	@NotBlank(message = "새 비밀번호 확인은 필수항목입니다.")
	private String newPassword2;
}