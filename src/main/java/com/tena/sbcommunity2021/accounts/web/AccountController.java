package com.tena.sbcommunity2021.accounts.web;

import com.tena.sbcommunity2021.accounts.domain.Account;
import com.tena.sbcommunity2021.accounts.dto.AccountDto;
import com.tena.sbcommunity2021.accounts.service.AccountService;
import com.tena.sbcommunity2021.accounts.validator.AccountDtoValidator;
import com.tena.sbcommunity2021.global.commons.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final ModelMapper modelMapper;
	private final AccountDtoValidator accountDtoValidator;

	@InitBinder("saveDto")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(accountDtoValidator);
	}

	@RequestMapping("/new")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseData<AccountDto.Response> createAccount(@Valid @ModelAttribute("saveDto") AccountDto.Save saveDto) {
		Account createdAccount = accountService.createAccount(saveDto);

		final AccountDto.Response response = modelMapper.map(createdAccount, AccountDto.Response.class); // Domain Object to DTO

		return ResponseData.of("S-1", "회원가입이 완료되었습니다.", response);
	}

	@GetMapping
	@ResponseBody
	public ResponseData<List<AccountDto.Response>> getAccounts() {
		List<Account> accounts = accountService.getAccounts();

		List<AccountDto.Response> body = accounts.stream()
				.map(account -> modelMapper.map(account, AccountDto.Response.class))
				.collect(Collectors.toList());
		// List<AccountDto.Response> body = modelMapper.map(accounts, new TypeToken<List<AccountDto.Response>>() {}.getType());

		return ResponseData.of("S-1", "회원 목록 입니다.", body);
	}

}
