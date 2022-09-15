package com.planner.service;

import java.util.Optional;

import com.planner.entity.Account;
import com.planner.form.PlannerUser;

public interface AccountService{

	public Optional<Account> findById(int theId);
	
	public Optional<Account> findFirstByEmail(String theEmail);
	
	public void save(PlannerUser plannerUser);
	
	public void deleteById(int theId);
}
