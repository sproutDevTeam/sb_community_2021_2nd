package com.tena.sbcommunity2021.global.commons;

import com.tena.sbcommunity2021.accounts.domain.Account;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAccount {

	private Account account;

	public boolean isAuthenticated() {
		return this.account != null;
	}

	public Long getAccountId() {
		return this.account != null ? this.account.getId() : null;
	}

}
