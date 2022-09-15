package com.planner.service;

import java.util.List;

import com.planner.entity.Event;
import com.planner.entity.Reminder;

public interface ReminderService {

	public Reminder findById(int theId);
	
	public List<Reminder> findByEvent(Event theEvent);
	
	public void save(Reminder theReminder);
	
	public void deleteById(int theId);
}
