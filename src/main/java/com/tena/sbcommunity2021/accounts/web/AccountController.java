package com.tena.sbcommunity2021.accounts.web;

import com.tena.sbcommunity2021.accounts.domain.Account;
import com.tena.sbcommunity2021.accounts.dto.AccountDto;
import com.tena.sbcommunity2021.accounts.service.AccountService;
import com.tena.sbcommunity2021.accounts.validator.AccountDtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
	public AccountDto.Response createAccount(@Valid @ModelAttribute("saveDto") AccountDto.Save saveDto) {
		Account createdAccount = accountService.createAccount(saveDto);

		return modelMapper.map(createdAccount, AccountDto.Response.class);
	}

}
