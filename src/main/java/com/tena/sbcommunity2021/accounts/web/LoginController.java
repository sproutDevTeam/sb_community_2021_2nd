package com.tena.sbcommunity2021.accounts.web;

import com.tena.sbcommunity2021.accounts.domain.Account;
import com.tena.sbcommunity2021.accounts.dto.AccountDto;
import com.tena.sbcommunity2021.accounts.dto.LoginForm;
import com.tena.sbcommunity2021.accounts.service.AccountService;
import com.tena.sbcommunity2021.global.commons.CurrentUser;
import com.tena.sbcommunity2021.global.commons.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

	private final AccountService accountService;
	private final CurrentUser currentUser;

	@RequestMapping("/login")
	@ResponseBody
	public ResponseData<AccountDto.Response> login(@Validated LoginForm loginForm) {

		if (currentUser.isAuthenticated()) {
			return ResponseData.of("F-3", "이미 로그인 중입니다.");
		}

		Account account = accountService.getAccountByUsername(loginForm.getUsername());

		if (account == null) {
			return ResponseData.of("F-1", "존재하지 않는 아이디입니다.");
		}

		if (!account.getPassword().equals(loginForm.getPassword())) {
			return ResponseData.of("F-2", "입력한 아이디 또는 비밀번호가 올바르지 않습니다.");
		}

		currentUser.setAccount(account);

		return ResponseData.of("S-1", String.format("%s님 환영합니다.", account.getNickname()));
	}

	@RequestMapping("/logout")
	@ResponseBody
	public ResponseData<AccountDto.Response> logout(HttpSession httpSession) {

		if (!currentUser.isAuthenticated()) {
			return ResponseData.of("S-1", "로그인 상태가 아닙니다.");
		}

		httpSession.invalidate();
		//currentUser.setAccount(null);

		return ResponseData.of("S-2", "로그아웃 되었습니다.");
	}

}
