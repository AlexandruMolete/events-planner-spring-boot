package com.planner.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

import com.planner.auxiliary.EventNotification;
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
	
	public List<EventNotification> getUpcomingEventsForCurrentDate(List<Event> theEvents){
		
		List<EventNotification> upcomingEvents = new ArrayList<>();
		LocalTime currentLocalTime = LocalTime.now();
		theEvents.stream()
		 .filter(event -> event.getDate().isEqual(LocalDate.now()))
		 .filter(event -> event.getTime().isAfter(currentLocalTime))
		 .forEach(event -> {
			 if(!event.getReminders().isEmpty()) {
				 List<LocalTime> reminderTimes = event.getReminders().stream()
				 					 			  .map(Reminder::getTime)
				 					 			  .distinct()
				 					 			  .collect(Collectors.toList());
				 long hoursDifference = currentLocalTime.until(event.getTime(), ChronoUnit.HOURS);
				 long minutesDifference = currentLocalTime.until(event.getTime(), ChronoUnit.MINUTES);
				 long secondsDifference = currentLocalTime.until(event.getTime(), ChronoUnit.SECONDS);;
				 reminderTimes.stream().forEach(reminderTime -> {
					 
					 String remainingTime = "";
					 int reminderHour = reminderTime.getHour();
					 int reminderMin = reminderTime.getMinute();
					 int reminderSec = reminderTime.getSecond();
					 if(hoursDifference < reminderHour) {
						 remainingTime = remainingTime + Integer.toString(reminderHour)+" hour(s) "
								 					   + (reminderMin > 0 ? Integer.toString(reminderMin)+" minute(s) " : "")
								 					   + (reminderSec > 0 ? Integer.toString(reminderSec)+" second(s)" : "");
					 }
					 else if(minutesDifference < reminderTime.getMinute()) {
						 remainingTime = remainingTime + Integer.toString(reminderMin) + " minute(s) "			 					   
			 					   					   + (reminderSec > 0 ? Integer.toString(reminderSec)+" second(s)" : "");
					 }
					 else if(secondsDifference < reminderTime.getSecond()) {
						 remainingTime = remainingTime + Integer.toString(reminderTime.getSecond())+" second(s)";
					 }
					 
					 if(!remainingTime.isEmpty()) {
						 upcomingEvents.add(new EventNotification(event.getName(),remainingTime));
					 }
				 });
			 }
		 });
		return upcomingEvents;
		
	}

	@GetMapping("/list")
	public String listEvents(@AuthenticationPrincipal UserDetails currentSessionAccount, Model theModel) {
		
		String accountEmail = currentSessionAccount.getUsername();
		
		Optional<Account> resultedAccount = Optional.empty();
		
		try {
			resultedAccount = accountService.findByEmail(accountEmail);
		}catch(Exception exp) {
			throw new RuntimeException("Could not list the events. The session has expired.");
		}
		
		Account currentAccount = new Account();
		
		if(resultedAccount.isPresent()) {
			currentAccount = resultedAccount.get();
		}
		else {
			throw new RuntimeException("Could not list the events. The account containing the e-mail address: " + accountEmail + ", does not exist.");
		}
		
		List<Event> theEvents = new ArrayList<>();
		
		try {
			theEvents = eventService.findByAccount(currentAccount);
		}catch(Exception exp){
			throw new RuntimeException("Could not list the events. The account of: " + 
										currentAccount.getFirstName() + " " + currentAccount.getLastName() + 
										", could not be found.");
		}
		
		List<String> theDates = theEvents.stream().map(Event::getDate).map(LocalDate::toString).distinct().collect(Collectors.toList());
		
		List<EventNotification> upcomingEvents = getUpcomingEventsForCurrentDate(theEvents);
		
		theModel.addAttribute("events", theEvents);
		theModel.addAttribute("dates",theDates);
		theModel.addAttribute("notifications", upcomingEvents);
		
		return "/events/list-events";
		
	}
	
	@GetMapping("/search")
	public String searchEventsByDate(@RequestParam("selectedEventDate") String theDate,
			@AuthenticationPrincipal UserDetails currentSessionAccount,
			Model theModel) {
		
		String accountEmail = currentSessionAccount.getUsername();
		
		Optional<Account> resultedAccount = Optional.empty();
		
		try {
			resultedAccount = accountService.findByEmail(accountEmail);
		}catch(Exception exp) {
			throw new RuntimeException("Could not list the events. The session has expired.");
		}
		
		Account currentAccount = new Account();
		
		if(resultedAccount.isPresent()) {
			currentAccount = resultedAccount.get();
		}
		else {
			throw new RuntimeException("Could not list the events. The account containing the e-mail address: " + accountEmail + ", does not exist.");
		}
		
		List<Event> theEvents = new ArrayList<>();
		
		try {
			theEvents = eventService.searchBy(currentAccount, theDate);
		}catch(Exception exp){
			throw new RuntimeException("Could not list the events. The events for the following date: " + theDate + ", could not be found.");
		}
		
		List<String> theDates = new ArrayList<String>();
		
		try {
			theDates = eventService.findByAccount(currentAccount).stream().map(Event::getDate)
								   .map(LocalDate::toString).distinct().collect(Collectors.toList());
		}catch(Exception exp) {
			throw new RuntimeException("Could not list the dates. The events for user: " +
										currentAccount.getFirstName() + " " + currentAccount.getLastName() +
										" could not be found.");
		}
		
		List<EventNotification> upcomingEvents = getUpcomingEventsForCurrentDate(theEvents);
		
		theModel.addAttribute("events", theEvents);
		theModel.addAttribute("dates",theDates);
		theModel.addAttribute("notifications", upcomingEvents);
		
		return "/events/list-events";
		
	}
	
	@GetMapping("/renderFormForAdd")
	public String renderFormForAdd(Model theModel) {
		
		PlannerEvent newEvent = new PlannerEvent();
		theModel.addAttribute("plannerEvent",newEvent);
		return "/events/event-form";
		
	}
	
	@GetMapping("/renderFormForUpdate")
	public String renderFormForUpdate(@RequestParam("eventId") int theId, Model theModel) {
		
		Event currentEvent = new Event();
		
		Optional<Event> foundEvent = eventService.findById(theId);
		
		if(foundEvent.isPresent()) {
			currentEvent = foundEvent.get();
		}
		else {
			throw new RuntimeException("Could not render the form to update the event. ID of " + theId + " was not found.");
		}
		
		PlannerEvent plannerEvent = new PlannerEvent();
		plannerEvent.setId(currentEvent.getId());
		plannerEvent.setName(currentEvent.getName());
		plannerEvent.setDate(currentEvent.getDate().toString());
		plannerEvent.setTime(currentEvent.getTime().toString());
		
		theModel.addAttribute("plannerEvent",plannerEvent);
		
		return "/events/event-form";
		
	}
	
	@PostMapping("/save")
	public String saveEvent(@Valid @ModelAttribute("plannerEvent") PlannerEvent eventDetailsFromForm,
			BindingResult theBindingResult,
			@AuthenticationPrincipal UserDetails currentSessionAccount) {
		
		if(theBindingResult.hasErrors()) {
			return "/events/event-form";
		}
		
		String accountEmail = currentSessionAccount.getUsername();
		
		Optional<Account> foundAccount = Optional.empty();
		
		try {
			foundAccount = accountService.findByEmail(accountEmail);
		}catch(Exception exp) {
			throw new RuntimeException("Could not save the event. The session has expired.");
		}
		
		Account currentAccount = new Account();
		
		if(foundAccount.isPresent()) {
			currentAccount = foundAccount.get();
		}
		else {
			throw new RuntimeException("Could not save or update the event. The account containing the e-mail address: " + accountEmail + ", does not exist.");
		}
		
		Event newEvent = new Event();
		
		if(eventDetailsFromForm.getId() != 0) {
			
			Optional<Event> foundEvent = Optional.empty();
			
			Event theEvent = new Event();
			
			try {
				
				foundEvent = eventService.findById(eventDetailsFromForm.getId());
				
				if(foundEvent.isPresent()) {
					theEvent = foundEvent.get();
				}
				else {
					throw new NoSuchElementException();
				}
				
			}catch(Exception exp) {
				
				throw new RuntimeException("Could not update the event. ID of " + eventDetailsFromForm.getId() + " was not found.");
				
			}
			
			newEvent = new Event(
					eventDetailsFromForm.getId(),
					eventDetailsFromForm.getName(),
					LocalDate.parse(eventDetailsFromForm.getDate()),
					LocalTime.parse(eventDetailsFromForm.getTime()),
					theEvent.getAccounts(),
					theEvent.getReminders()
					);
			
		}
		else {
			newEvent = new Event(
					eventDetailsFromForm.getName(),
					LocalDate.parse(eventDetailsFromForm.getDate()),
					LocalTime.parse(eventDetailsFromForm.getTime()),
					Arrays.asList(currentAccount)
					);
		}
		
		try {
			eventService.save(newEvent);
		}catch(Exception exp) {
			throw new RuntimeException("Could not save the event.");
		}
		
		return "redirect:/events/list";
		
	}
	
	@GetMapping("/delete")
	public String deleteEvent(@RequestParam("eventId") int theId) {
		
		try {
			eventService.deleteById(theId);
		}catch(Exception exp) {
			throw new RuntimeException("Could not delete the event with ID of " + theId + ".");
		}
		return "redirect:/events/list";
		
	}
	
	@GetMapping("/renderPageForInvitation")
	public String renderPageForInvitation(@RequestParam("eventId") int theId, Model theModel) {
		
		Optional<Event> foundEvent = Optional.empty();
		
		Event currentEvent = new Event();
		
		try {
			
			foundEvent = eventService.findById(theId);
			
			if(foundEvent.isPresent()) {
				currentEvent = foundEvent.get();
			}
			else {
				throw new NoSuchElementException();
			}
			
		}catch(Exception exp) {
			
			throw new RuntimeException("Error while displaying the form to invite users. The event with the ID of " + theId + " was not found.");
			
		}
		
		List<Account> availableAccountsForInvitation = new ArrayList<Account>();
		
		try {
			availableAccountsForInvitation = accountService.findAll();
		} catch (Exception exp) {
			throw new RuntimeException("Failed to find all accounts, while accessing the form to invite users.");
		}
		
		availableAccountsForInvitation.removeAll(currentEvent.getAccounts());
		
		boolean isAnyAccountAvailable = !availableAccountsForInvitation.isEmpty();
		
		theModel.addAttribute("selectedEvent",currentEvent);
		theModel.addAttribute("isAnyAccountAvailable",isAnyAccountAvailable);
		theModel.addAttribute("availableAccounts",availableAccountsForInvitation);
		
		return "/events/invite-user";
		
	}
	
	@PostMapping("/invite")
	public String inviteUser(@RequestParam("eventId") int theEventId, @RequestParam("selectedAccountId") int theInvitedAccountId) {

		Optional<Event> foundEvent = Optional.empty();
		
		Event currentEvent = new Event();
		
		try {
			
			foundEvent = eventService.findById(theEventId);
			
			if(foundEvent.isPresent()) {
				currentEvent = foundEvent.get();
			}
			else {
				throw new NoSuchElementException();
			}
			
		}catch(Exception exp) {
			
			throw new RuntimeException("Could not invite the user. The event with the ID of " + theEventId + " was not found.");
			
		}
		
		Account theInvitedAccount = new Account();
		
		Optional<Account> foundAccount = Optional.empty();
		
		try {
			
			foundAccount = accountService.findById(theInvitedAccountId);
			
			if (foundAccount.isPresent()) {
				theInvitedAccount = foundAccount.get();
			} else {
				throw new NoSuchElementException();
			}
			
		} catch (Exception exp) {
			throw new RuntimeException("Could not invite the user. Account ID of " + theInvitedAccountId + " was not found.");
		}
		
		Collection<Account> newListOfAccountsForCurrentEvent = currentEvent.getAccounts();
		
		newListOfAccountsForCurrentEvent.add(theInvitedAccount);
		
		currentEvent.setAccounts(newListOfAccountsForCurrentEvent);
		
		try {
			eventService.save(currentEvent);
		} catch (Exception e) {
			throw new RuntimeException("Could not invite the user. Error updating the event with ID of " + theEventId + ".");
		}
		
		return "redirect:/events/renderPageForInvitation?eventId="+Integer.toString(theEventId);
		
	}
	
	@PostMapping("/decline")
	public String declineInvitation(@RequestParam("eventId") int theEventId,
			@AuthenticationPrincipal UserDetails currentSessionAccount) {
		
		Optional<Event> foundEvent = Optional.empty();
		
		Event currentEvent = new Event();
		
		try {
			
			foundEvent = eventService.findById(theEventId);
			
			if(foundEvent.isPresent()) {
				currentEvent = foundEvent.get();
			}
			else {
				throw new NoSuchElementException();
			}
			
		}catch(Exception exp) {
			
			throw new RuntimeException("Error declining the invitation. The event with the ID of " + theEventId + " was not found.");
			
		}
		
		String guestEmail = currentSessionAccount.getUsername();
		
		Account theGuestAccount = new Account();
		
		Optional<Account> foundAccount = Optional.empty();
		
		try {
			
			foundAccount = accountService.findByEmail(guestEmail);
			
			if (foundAccount.isPresent()) {
				theGuestAccount = foundAccount.get();
			} else {
				throw new NoSuchElementException();
			}
			
		} catch (Exception exp) {
			throw new RuntimeException("Error declining the invitation from guest account. The e-mail address of " + guestEmail + " was not found.");
		}
		Collection<Account> modifiedListOfAccounts = currentEvent.getAccounts();
		modifiedListOfAccounts.remove(theGuestAccount);
		currentEvent.setAccounts(modifiedListOfAccounts);
		try {
			eventService.save(currentEvent);
		} catch (Exception exp) {
			throw new RuntimeException("Error declining the invitation while updating the event with ID of " + theEventId + ".");
		}

		return "redirect:/events/list";
		
	}
	
}
