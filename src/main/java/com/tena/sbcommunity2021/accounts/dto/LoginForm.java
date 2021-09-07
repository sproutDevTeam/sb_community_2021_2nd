package com.tena.sbcommunity2021.accounts.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LoginForm {
	@NotBlank(message = "로그인 아이디를 입력해주세요.")
	private String username;

	@NotBlank(message = "로그인 비밀번호를 입력해주세요.")
	private String password;
}
