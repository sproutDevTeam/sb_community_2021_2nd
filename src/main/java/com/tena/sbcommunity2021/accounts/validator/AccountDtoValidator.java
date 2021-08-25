package com.tena.sbcommunity2021.accounts.validator;

import com.tena.sbcommunity2021.accounts.dto.AccountDto;
import com.tena.sbcommunity2021.accounts.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class AccountDtoValidator implements Validator {

	private final AccountRepository accountRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(AccountDto.Save.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AccountDto.Save saveDto = (AccountDto.Save) target;

		if (accountRepository.existsByUsername(saveDto.getUsername())) {
			errors.rejectValue("username", "duplicate.username", new Object[]{saveDto.getUsername()}, "이미 사용 중인 아이디입니다.");
		}

		if (accountRepository.existsByEmail(saveDto.getEmail())) {
			errors.rejectValue("email", "duplicate.email", new Object[]{saveDto.getEmail()}, "이미 사용 중인 이메일입니다.");
		}

	}
}
