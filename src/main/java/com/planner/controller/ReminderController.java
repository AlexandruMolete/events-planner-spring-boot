package com.planner.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
		
		Event currentEvent = eventService.findById(theEventId);
		List<Reminder> theReminders = reminderService.findByEvent(currentEvent);
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
	public String renderFormForAdd(@RequestParam("selectedEventId") int theId, Model theModel) {
		
		PlannerReminder newReminder = new PlannerReminder();
		newReminder.setEventId(theId);
		theModel.addAttribute("plannerReminder",newReminder);
		return "/reminders/reminder-form";
		
	}
	
	@GetMapping("/renderFormForUpdate")
	public String renderFormForUpdate(@RequestParam("reminderId") int theId, Model theModel) {
		
		Reminder currentReminder = reminderService.findById(theId);
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
			Reminder theReminder = reminderService.findById(reminderDetailsFromForm.getId());
			eventOfReminder = theReminder.getEvent();
			newReminder = new Reminder(
					reminderDetailsFromForm.getId(),
					LocalTime.parse(reminderDetailsFromForm.getTime()),
					eventOfReminder
					);
		}
		else {
			eventOfReminder = eventService.findById(reminderDetailsFromForm.getEventId());
			newReminder = new Reminder(
					LocalTime.parse(reminderDetailsFromForm.getTime()),
					eventOfReminder
					);
		}
		
		reminderService.save(newReminder);
		return "redirect:/reminders/list?currentEventId="+Integer.toString(eventOfReminder.getId());
		
	}
	
	@GetMapping("/delete")
	public String deleteReminder(@RequestParam("reminderId") int theId) {
		
		Reminder reminderToDelete = reminderService.findById(theId);
		reminderService.deleteById(theId);
		return "redirect:/reminders/list?currentEventId="+Integer.toString(reminderToDelete.getEvent().getId());
		
	}
}
