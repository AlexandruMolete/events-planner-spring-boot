package com.planner.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planner.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer>{

	public Optional<Account> findFirstByEmail(String email);
}
