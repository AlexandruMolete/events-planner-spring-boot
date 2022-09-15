package com.planner.controller;

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

import com.planner.entity.Account;
import com.planner.form.PlannerUser;
import com.planner.service.AccountService;

@Controller
@RequestMapping("/accounts")
public class AccountController {

	private AccountService accountService;

	@Autowired
	public AccountController(AccountService theAccountService) {
		accountService = theAccountService;
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/delete")
	public String deleteAccount(@RequestParam("accountId") int theId) {
		
		accountService.deleteById(theId);
		
		return "redirect:/accounts/renderLogInPage";
		
	}
	
	@GetMapping("/renderLogInPage")
	public String renderLogInPage() {
		
		return "/accounts/login-page";
		
	}	
	
	@GetMapping("/logout")
	public String logOut(Model theModel) {
		
		theModel.addAttribute("loginMessage", "logout");
		return "/accounts/login-page";
	}
	
	@GetMapping("/authenticateTheAccount")
	public String authentification(@RequestParam("email") String theEmail, @RequestParam("password") String thePassword, Model theModel) {
		
		Optional<Account> result = accountService.findFirstByEmail(theEmail);
		Account theAccount = result.get();
		if(result.isPresent()) {
			if(theAccount.getPassword().compareTo(thePassword) != 0) {
				theModel.addAttribute("loginMessage","error");
				return "/accounts/login-page";
			}
		}
		else {
			theModel.addAttribute("loginMessage", "error");
			return "/accounts/login-page";
		}
		
		return "redirect:/events/list?currentAccountId="+Integer.toString(theAccount.getId());
	}
	
	@GetMapping("/renderRegistrationForm")
	public String renderLogInPage(Model theModel) {
		
		theModel.addAttribute("plannerUser", new PlannerUser());
		
		return "/accounts/registration-form";
	}

	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(
				@Valid @ModelAttribute("plannerUser") PlannerUser thePlannerUser, 
				BindingResult theBindingResult, 
				Model theModel) {
		
		String userEmail = thePlannerUser.getEmail();
		// form validation
		 if (theBindingResult.hasErrors()){
			 
			 return "/accounts/registration-form";
			 
	     }

		// check the database if user already exists
        Optional<Account> existingAccount = accountService.findFirstByEmail(userEmail);
        if (existingAccount.isPresent()){
        	theModel.addAttribute("plannerUser", new PlannerUser());
			theModel.addAttribute("registrationError", "Email address already exists.");
        	return "/accounts/registration-form";
        }
        
        // create user account        						
        accountService.save(thePlannerUser);
        
        return "/accounts/registration-confirmation";		
	}
}
