package com.tena.sbcommunity2021.accounts.repository;

import com.tena.sbcommunity2021.accounts.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Transactional(readOnly = true)
public interface AccountRepository {

	void save(Account account);

	Account findById(Long id);

	boolean existsById(Long id);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

}
