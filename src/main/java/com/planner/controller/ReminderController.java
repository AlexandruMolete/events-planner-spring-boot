package com.planner.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

import com.planner.auxiliary.ReminderTime;
import com.planner.entity.Event;
import com.planner.entity.Reminder;
import com.planner.form.PlannerReminder;
import com.planner.service.EventService;
import com.planner.service.ReminderService;

@Controller
@RequestMapping("/reminders")
public class ReminderController {
	
	private ReminderService reminderService;
	
	private EventService eventService;
	
	@Autowired
	public ReminderController(ReminderService theReminderService, EventService theEventService) {
		reminderService = theReminderService;
		eventService = theEventService;
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/list")
	public String listReminders(@RequestParam("currentEventId") int theEventId, Model theModel) {
		
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
			
		} catch(Exception exp) {
			
			throw new RuntimeException("Could not list the reminders. The event with ID of " + theEventId + " was not found.");
			
		}
		
		List<Reminder> theReminders = new ArrayList<Reminder>();
		
		try {
			theReminders = reminderService.findByEvent(currentEvent);
		} catch (Exception exp) {
			throw new RuntimeException("Could not list the reminders. Error occured while trying to find the reminders of event: " + currentEvent.getName());
		}
		
		List<ReminderTime> reminderTimes = new ArrayList<>();
		
		theReminders.forEach(reminder -> {
			LocalTime timer = reminder.getTime();
			int reminderHour = timer.getHour();
		    int reminderMin = timer.getMinute();
			int reminderSec = timer.getSecond();
			String stringTimer = (reminderHour > 0 ? Integer.toString(reminderHour)+" hour(s) " : "")
					   + (reminderMin > 0 ? Integer.toString(reminderMin)+" minute(s) " : "")
					   + (reminderSec > 0 ? Integer.toString(reminderSec)+" second(s)" : "");
			reminderTimes.add(new ReminderTime(reminder.getId(),stringTimer));
		});
		
		theModel.addAttribute("selectedEvent", currentEvent);
		theModel.addAttribute("reminders", reminderTimes);
		
		return "/reminders/list-reminders";
		
	}
	
	@GetMapping("/renderFormForAdd")
	public String renderFormForAdd(@RequestParam("selectedEventId") int theEventId, Model theModel) {
		
		PlannerReminder newReminder = new PlannerReminder();
		newReminder.setEventId(theEventId);
		theModel.addAttribute("plannerReminder",newReminder);
		theModel.addAttribute("eventId",theEventId);
		return "/reminders/reminder-form";
		
	}
	
	@GetMapping("/renderFormForUpdate")
	public String renderFormForUpdate(@RequestParam("reminderId") int theId, Model theModel) {
		
		Optional<Reminder> foundReminder = Optional.empty();
		
		Reminder currentReminder = new Reminder();
		
		try {
			foundReminder = reminderService.findById(theId);
			
			if (foundReminder.isPresent()) {
				currentReminder = foundReminder.get();
			} else {
				throw new NoSuchElementException();
			}
			
		} catch (Exception exp) {
			throw new RuntimeException("Could not render form to update reminder with ID of " + theId + ".");
		}
		
		Event eventOfReminder = currentReminder.getEvent();
		PlannerReminder plannerReminder = new PlannerReminder();
		plannerReminder.setId(currentReminder.getId());
		plannerReminder.setTime(currentReminder.getTime().toString());
		theModel.addAttribute("plannerReminder",plannerReminder);
		theModel.addAttribute("eventId",eventOfReminder.getId());
		return "/reminders/reminder-form";
		
	}
	
	@PostMapping("/save")
	public String saveReminder(@Valid @ModelAttribute("plannerReminder") PlannerReminder reminderDetailsFromForm, BindingResult theBindingResult) {
		
		if(theBindingResult.hasErrors()) {
			return "/reminders/reminder-form";
		}
		
		Event eventOfReminder = new Event();
		
		Reminder newReminder = new Reminder();
		
		if(reminderDetailsFromForm.getId() != 0) {
			
			Optional<Reminder> foundReminder = Optional.empty();
			
			Reminder oldReminder = new Reminder();
			
			try {
				foundReminder = reminderService.findById(reminderDetailsFromForm.getId());
				if (foundReminder.isPresent()) {
					oldReminder = foundReminder.get();
				} else {
					throw new NoSuchElementException();
				}
			} catch (Exception e) {
				throw new RuntimeException("Could not update the reminder. Error finding the reminder with ID of " + reminderDetailsFromForm.getId());
			}
			
			eventOfReminder = oldReminder.getEvent();
			
			newReminder = new Reminder(
					reminderDetailsFromForm.getId(),
					LocalTime.parse(reminderDetailsFromForm.getTime()),
					eventOfReminder
					);
		}
		else {
			
			Optional<Event> foundEvent = Optional.empty();
			
			try {
				foundEvent = eventService.findById(reminderDetailsFromForm.getEventId());
				if (foundEvent.isPresent()) {
					eventOfReminder = foundEvent.get();
				} else {
					throw new NoSuchElementException();
				}
			} catch (Exception e) {
				throw new RuntimeException("Could not associate the new reminder to the event of ID " + 
							reminderDetailsFromForm.getEventId() + ". No such event could be found.");
			}

			newReminder = new Reminder(
					LocalTime.parse(reminderDetailsFromForm.getTime()),
					eventOfReminder
					);
		}
		
		try {
			reminderService.save(newReminder);
		} catch (Exception e) {
			throw new RuntimeException("Could not save or update the new reminder.");
		}
		
		
		return "redirect:/reminders/list?currentEventId="+Integer.toString(eventOfReminder.getId());
		
	}
	
	@GetMapping("/delete")
	public String deleteReminder(@RequestParam("reminderId") int theId) {
		
		Optional<Reminder> foundReminder = Optional.empty();
		
		Reminder reminderToBeDeleted = new Reminder();
		
		try {
			
			foundReminder = reminderService.findById(theId);
			
			if (foundReminder.isPresent()) {
				reminderToBeDeleted = foundReminder.get();
			} else {
				throw new NoSuchElementException();
			}
			
			reminderService.deleteById(theId);
			
		} catch (Exception exp) {
			throw new RuntimeException("Could not delete reminder with ID of " + theId + ". No such reminder was found.");
		}
		
		return "redirect:/reminders/list?currentEventId="+Integer.toString(reminderToBeDeleted.getEvent().getId());
		
	}
}
