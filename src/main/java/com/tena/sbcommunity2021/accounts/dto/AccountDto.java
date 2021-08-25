package com.tena.sbcommunity2021.accounts.dto;

import lombok.*;

import java.time.LocalDateTime;

public class AccountDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Save {
		private String username;
		private String password;
		private String name;
		private String nickname;
		private String mobileNumber;
		private String email;
	}

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

		private LocalDateTime regDate;
		private LocalDateTime updateDate;
		private boolean delStatus;
		private LocalDateTime delDate;
	}

}