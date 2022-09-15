package com.planner.service;

import java.util.List;

import com.planner.entity.Account;
import com.planner.entity.Event;

public interface EventService {

	public Event findById(int theId);
	
	public List<Event> findByAccount(Account account);
	
	public List<Event> searchBy(Account account, String dateOption);
	
	public void save(Event theEvent);
	
	public void deleteById(int theId);
}
