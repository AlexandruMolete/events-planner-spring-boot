package com.planner.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.planner.dao.EventRepository;
import com.planner.entity.Account;
import com.planner.entity.Event;

@Service
public class EventServiceImpl implements EventService {

	private EventRepository eventRepository;
	
	public EventServiceImpl(EventRepository theEventRepository) {
		eventRepository = theEventRepository;
	}

	@Override
	public Event findById(int theId) {
		Optional<Event> theEvent = eventRepository.findById(theId);
		if(theEvent.isEmpty()) {
			throw new RuntimeException("Did not find event id - " + theId);
		}
		return theEvent.get();
	}
	
	@Override
	public List<Event> findByAccount(Account account) {
		
		return eventRepository.findByAccountOrderByDateAsc(account);
	}

	@Override
	public List<Event> searchBy(Account account, String dateOption) {
		List<Event> results = new ArrayList<>();
		if(dateOption.compareTo("all") == 0) {
			results = new ArrayList<>(eventRepository.findByAccountOrderByDateAsc(account));
		}
		else if(dateOption.compareTo("now") == 0) {
			results = new ArrayList<>(eventRepository.findByAccountAndDateOrderByTimeAsc(account, LocalDate.now()));
		}
		else {
			results = new ArrayList<>(eventRepository.findByAccountAndDateOrderByTimeAsc(account, LocalDate.parse(dateOption)));
		}
		return results;
	}

	@Override
	public void save(Event theEvent) {
		eventRepository.save(theEvent);

	}

	@Override
	public void deleteById(int theId) {
		eventRepository.deleteById(theId);

	}

}
