package com.tena.sbcommunity2021.accounts.service;

import com.tena.sbcommunity2021.accounts.domain.Account;
import com.tena.sbcommunity2021.accounts.dto.AccountDto;
import com.tena.sbcommunity2021.accounts.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

	private final AccountRepository accountRepository;
	private final ModelMapper modelMapper;

	@Transactional
	public Account createAccount(AccountDto.Save saveDto) {
		Account account = modelMapper.map(saveDto, Account.class);

		accountRepository.save(account);

		Account savedAccount = accountRepository.findById(account.getId());

		return savedAccount;
	}

}
