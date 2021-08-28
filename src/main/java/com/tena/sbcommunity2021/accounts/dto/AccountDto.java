package com.tena.sbcommunity2021.accounts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class AccountDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Save {

		@NotBlank(message = "로그인 아이디를 입력해주세요.")
		@Length(min = 3, max = 20, message = "3 ~ 20자 이내로 입력해주세요.")
		@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
		private String username;

		@NotBlank(message = "로그인 비밀번호를 입력해주세요.")
		@Length(min = 8, max = 50, message = "비밀번호는 8자 이상이어야 합니다.") //TODO 비밀번호 해싱
		private String password;

		@NotBlank(message = "별명을 입력해주세요.")
		private String name;

		@NotBlank(message = "이름을 입력해주세요.")
		private String nickname;

		@NotBlank(message = "연락처를 입력해주세요.")
		private String mobileNumber;

		@Email(message = "이메일이 올바르지 않습니다.")
		@NotBlank(message = "이메일을 입력해주세요.")
		private String email;

		@Builder.Default
		private LocalDateTime updateDate = LocalDateTime.now();

	}

	@JsonIgnoreProperties({"password"})
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Response {
		private Long id;
		private String username;
		private String password;
		private int authLevel;
		private String name;
		private String nickname;
		private String mobileNumber;
		private String email;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime regDate;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime updateDate;

		private boolean delStatus;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime delDate;
	}

}