package com.planner.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planner.dao.AccountRepository;
import com.planner.entity.Account;
import com.planner.entity.Event;
import com.planner.form.PlannerUser;

@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository accountRepository;
	
	@Autowired
	public AccountServiceImpl(AccountRepository theAccountRepository) {
		accountRepository = theAccountRepository;
	}

	@Override
	public Optional<Account> findById(int theId) {
		Optional<Account> result = accountRepository.findById(theId);
		if (result.isEmpty()){
			throw new RuntimeException("Did not find account id - " + theId);
		}
		return result;
	}

	@Override
	public Optional<Account> findFirstByEmail(String theEmail) {
		
		return accountRepository.findFirstByEmail(theEmail);
	}

	@Override
	public void save(PlannerUser plannerUser) {

		Account account = new Account(plannerUser.getEmail(),plannerUser.getPassword(),plannerUser.getFirstName(),plannerUser.getLastName(),new ArrayList<Event>());
//		account.setEmail(plannerUser.getEmail());
//		account.setPassword(plannerUser.getPassword());
//		account.setFirstName(plannerUser.getFirstName());
//		account.setLastName(plannerUser.getLastName());
		accountRepository.save(account);
	}

	@Override
	public void deleteById(int theId) {
		accountRepository.deleteById(theId);
	}


}
