package com.tena.sbcommunity2021.accounts.repository;

import com.tena.sbcommunity2021.accounts.domain.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountRepository {

	void save(Account account);

	Account findById(Long id);

}
