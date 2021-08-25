package com.tena.sbcommunity2021.accounts.domain;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

	private Long id;

	private String username;

	private String password;

	private int authLevel;

	private String nickname;

	private String name;

	private String mobileNumber;

	private String email;

	private boolean delStatus;

	private LocalDateTime delDate;

	private LocalDateTime regDate;

	private LocalDateTime updateDate;

}