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
	public Optional<Event> findById(int theId) {
		
		Optional<Event> theEvent = eventRepository.findById(theId);
		return theEvent;
	}
	
	@Override
	public List<Event> findByAccount(Account account) {
		
		return eventRepository.findByAccountsOrderByDateAsc(account);
	}

	@Override
	public List<Event> searchBy(Account account, String dateOption) {
		List<Event> results = new ArrayList<>();
		if(dateOption.compareTo("all") == 0) {
			results = eventRepository.findByAccountsOrderByDateAsc(account);
		}
		else if(dateOption.compareTo("now") == 0) {
			results = eventRepository.findByAccountsAndDateOrderByTimeAsc(account, LocalDate.now());
		}
		else {
			results = eventRepository.findByAccountsAndDateOrderByTimeAsc(account, LocalDate.parse(dateOption));
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
