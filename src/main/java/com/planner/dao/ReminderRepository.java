package com.planner.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planner.entity.Event;
import com.planner.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Integer>{

	public List<Reminder> findByEventOrderByTimeAsc(Event theEvent);
}
