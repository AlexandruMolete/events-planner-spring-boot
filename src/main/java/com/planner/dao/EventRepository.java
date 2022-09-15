package com.planner.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planner.entity.Account;
import com.planner.entity.Event;

public interface EventRepository extends JpaRepository<Event, Integer>{

	public List<Event> findByAccountOrderByDateAsc(Account account);
	
	public List<Event> findByAccountAndDateOrderByTimeAsc(Account account, LocalDate date);
}
