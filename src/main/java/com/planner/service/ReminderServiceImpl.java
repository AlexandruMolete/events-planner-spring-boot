package com.planner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.planner.dao.ReminderRepository;
import com.planner.entity.Event;
import com.planner.entity.Reminder;

@Service
public class ReminderServiceImpl implements ReminderService {

	private ReminderRepository reminderRepository;
	
	public ReminderServiceImpl(ReminderRepository theReminderRepository) {
		reminderRepository = theReminderRepository;
	}

	@Override
	public Reminder findById(int theId) {
		Optional<Reminder> result = reminderRepository.findById(theId);
		if(result.isEmpty()) {
			throw new RuntimeException("Did not find reminder id - " + theId);
		}
		return result.get();
	}

	@Override
	public List<Reminder> findByEvent(Event theEvent) {
		
		return reminderRepository.findByEventOrderByTimeAsc(theEvent);
	}

	@Override
	public void save(Reminder theReminder) {
		
		reminderRepository.save(theReminder);

	}

	@Override
	public void deleteById(int theId) {
		
		reminderRepository.deleteById(theId);

	}

}
