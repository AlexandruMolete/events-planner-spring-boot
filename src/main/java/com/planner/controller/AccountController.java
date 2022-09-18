package com.planner.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.planner.entity.Account;
import com.planner.entity.Event;
import com.planner.entity.Role;
import com.planner.service.AccountService;
import com.planner.service.EventService;

@Controller
@RequestMapping("/accounts")
public class AccountController {

	private AccountService accountService;
	
	private EventService eventService;

	@Autowired
	public AccountController(AccountService theAccountService, EventService theEventService) {
		accountService = theAccountService;
		eventService = theEventService;
	}

	@GetMapping("/delete")
	public String deleteAccount(@RequestParam("accountId") int theId) {
		
		Account theAccount = accountService.findById(theId).get();
		boolean isAccountGuest = theAccount.getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains("ROLE_GUEST");
		if(isAccountGuest) {
			accountService.deleteById(theId);
		}
		else {
			List<Event> hostAccountEvents = eventService.findByAccount(theAccount);
			hostAccountEvents.forEach(event -> eventService.deleteById(event.getId()));
			accountService.deleteById(theId);
		}
		return "redirect:/accounts/renderLogInPage";
		
	}
	
	@GetMapping("/renderLogInPage")
	public String renderLogInPage() {
		
		return "/accounts/login-page";
		
	}	

}
