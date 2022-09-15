package com.planner.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.planner.auxiliary.Notification;
import com.planner.entity.Account;
import com.planner.entity.Event;
import com.planner.entity.Reminder;
import com.planner.form.PlannerEvent;
import com.planner.service.AccountService;
import com.planner.service.EventService;

@Controller
@RequestMapping("/events")
public class EventController {

	private EventService eventService;
	
	private AccountService accountService;

	@Autowired
	public EventController(EventService theEventService, AccountService theAccountService) {
		eventService = theEventService;
		accountService = theAccountService;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	public List<Notification> getUpcomingEventsForCurrentDate(List<Event> theEvents){
		List<Notification> upcomingEvents = new ArrayList<>();
		theEvents.stream()
		 .filter(event -> event.getDate().isEqual(LocalDate.now()))
		 .filter(event -> event.getTime().isAfter(LocalTime.now()))
		 .forEach(event -> {
			 if(!event.getReminders().isEmpty()) {
				 List<LocalTime> reminderTimes = event.getReminders().stream()
				 					 			  .map(Reminder::getTime)
				 					 			  .distinct()
				 					 			  .collect(Collectors.toList());
				 reminderTimes.stream().forEach(reminderTime -> {
					 String remainingTime = "";
					 if((event.getTime().getHour() - LocalTime.now().getHour()) <= reminderTime.getHour()) {
						 remainingTime = remainingTime + Integer.toString(reminderTime.getHour())+" hour(s) ";
					 }
					 if((event.getTime().getMinute() - LocalTime.now().getMinute()) <= reminderTime.getMinute()) {
						 remainingTime = remainingTime + Integer.toString(reminderTime.getMinute())+" minute(s) ";
					 }
					 if((event.getTime().getSecond() - LocalTime.now().getSecond()) <= reminderTime.getSecond()) {
						 remainingTime = remainingTime + Integer.toString(reminderTime.getSecond())+" second(s)";
					 }
					 upcomingEvents.add(new Notification(event.getEventTitle(),remainingTime));
				 });
			 }
		 });
		return upcomingEvents;
	}

	@GetMapping("/list")
	public String listEvents(@RequestParam("currentAccountId") int accountId, Model theModel) {
		
		Account currentAccount = accountService.findById(accountId).get();
		List<Event> theEvents = eventService.findByAccount(currentAccount);
		List<String> theDates = theEvents.stream().map(Event::getDate).map(LocalDate::toString).distinct().collect(Collectors.toList());
		List<Notification> upcomingEvents = getUpcomingEventsForCurrentDate(theEvents);
		
		theModel.addAttribute("currentAccount", currentAccount);
		theModel.addAttribute("events", theEvents);
		theModel.addAttribute("dates",theDates);
		theModel.addAttribute("notifications", upcomingEvents);
		
		return "/events/list-events";
	}
	
	@GetMapping("/search")
	public String searchEventsByDate(@RequestParam("accountId") int theId, @RequestParam("selectedEventDate") String theDate, Model theModel) {
		Account currentAccount = accountService.findById(theId).get();
		List<Event> theEvents = eventService.searchBy(currentAccount, theDate);
		List<String> theDates = eventService.findByAccount(currentAccount).stream().map(Event::getDate)
									.map(LocalDate::toString).distinct().collect(Collectors.toList());
		List<Notification> upcomingEvents = getUpcomingEventsForCurrentDate(theEvents);
		theModel.addAttribute("currentAccount", currentAccount);
		theModel.addAttribute("events", theEvents);
		theModel.addAttribute("dates",theDates);
		theModel.addAttribute("notifications", upcomingEvents);
		return "/events/list-events";
	}
	
	@GetMapping("/renderFormForAdd")
	public String renderFormForAdd(@RequestParam("currentAccountId") int theId, Model theModel) {
		PlannerEvent newEvent = new PlannerEvent();
		newEvent.setAccountId(theId);
		theModel.addAttribute("plannerEvent",newEvent);
		return "/events/event-form";
	}
	
	@GetMapping("/renderFormForUpdate")
	public String renderFormForUpdate(@RequestParam("eventId") int theId, Model theModel) {
		Event currentEvent = eventService.findById(theId);
		Account accountOfEvent = currentEvent.getAccount();
		PlannerEvent plannerEvent = new PlannerEvent();
		plannerEvent.setId(currentEvent.getId());
		plannerEvent.setEventTitle(currentEvent.getEventTitle());
		plannerEvent.setDate(currentEvent.getDate().toString());
		plannerEvent.setTime(currentEvent.getTime().toString());
		theModel.addAttribute("plannerEvent",plannerEvent);
		theModel.addAttribute("accountId",accountOfEvent.getId());
		return "/events/event-form";
	}
	
	@PostMapping("/save")
	public String saveEvent(@Valid @ModelAttribute("plannerEvent") PlannerEvent eventDetailsFromForm, BindingResult theBindingResult) {
		
		if(theBindingResult.hasErrors()) {
			return "/events/event-form";
		}
		
		Account accountOfEvent = new Account();
		Event newEvent = new Event();
		
		if(eventDetailsFromForm.getId() != 0) {
			Event theEvent = eventService.findById(eventDetailsFromForm.getId());
			accountOfEvent = theEvent.getAccount();
			newEvent = new Event(
					eventDetailsFromForm.getId(),
					eventDetailsFromForm.getEventTitle(),
					LocalDate.parse(eventDetailsFromForm.getDate()),
					LocalTime.parse(eventDetailsFromForm.getTime()),
					accountOfEvent,
					theEvent.getReminders()
					);
		}
		else {
			accountOfEvent = accountService.findById(eventDetailsFromForm.getAccountId()).get();
			newEvent = new Event(
					eventDetailsFromForm.getEventTitle(),
					LocalDate.parse(eventDetailsFromForm.getDate()),
					LocalTime.parse(eventDetailsFromForm.getTime()),
					accountOfEvent
					);
		}
		
		eventService.save(newEvent);
		return "redirect:/events/list?currentAccountId="+Integer.toString(accountOfEvent.getId());
	}
	
	@GetMapping("/delete")
	public String deleteEvent(@RequestParam("eventId") int theId) {
		Event eventToDelete = eventService.findById(theId);
		eventService.deleteById(theId);
		return "redirect:/events/list?currentAccountId="+Integer.toString(eventToDelete.getAccount().getId());
	}
	
}
