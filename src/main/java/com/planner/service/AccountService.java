package com.planner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.planner.entity.Account;
import com.planner.form.PlannerUser;

public interface AccountService extends UserDetailsService{

	public Optional<Account> findById(int theId);
	
	public Optional<Account> findByEmail(String theEmail);
	
	public List<Account> findAll();
	
	public void save(PlannerUser plannerUser);
	
	public void deleteById(int theId);
}
