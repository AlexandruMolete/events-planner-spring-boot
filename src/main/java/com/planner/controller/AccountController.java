package com.planner.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
		
		Optional<Account> foundAccount = Optional.empty();
		
		Account theAccount = new Account();
		
		try {
			foundAccount = accountService.findById(theId);
			if(foundAccount.isPresent()) {
				theAccount = foundAccount.get();
			} else {
				throw new NoSuchElementException();
			}
		} catch (Exception exp) {
			throw new RuntimeException("Error while deleting the account. User with ID of "+ theId + "was not found.");
		}
		
		boolean isAccountGuest = theAccount.getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains("ROLE_GUEST");
		
		try {
			if(isAccountGuest) {
				accountService.deleteById(theId);
			}
			else {
				List<Event> hostAccountEvents = eventService.findByAccount(theAccount);
				hostAccountEvents.forEach(event -> eventService.deleteById(event.getId()));
				accountService.deleteById(theId);
			}
		} catch (Exception exp) {
			throw new RuntimeException("Error during the process of deleting the account of " + 
										theAccount.getFirstName() + " " + theAccount.getLastName() + "was not found.");
		}
		
		return "redirect:/accounts/renderLogInPage";
		
	}
	
	@GetMapping("/renderLogInPage")
	public String renderLogInPage() {
		
		return "/accounts/login-page";
		
	}	
	
	@GetMapping("/accessDenied")
	public String renderAccessDeniedPage() {
		
		return "/accounts/access-denied";
		
	}

}
