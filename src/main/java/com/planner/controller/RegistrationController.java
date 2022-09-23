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

import com.planner.entity.Account;
import com.planner.form.PlannerUser;
import com.planner.service.AccountService;

@Controller
@RequestMapping("/accounts/register")
public class RegistrationController {

	private AccountService accountService;

	@Autowired
	public RegistrationController(AccountService theAccountService) {
		accountService = theAccountService;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
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

		 if (theBindingResult.hasErrors()){
			 
			 return "/accounts/registration-form";
			 
	     }

        Optional<Account> existingAccount = accountService.findByEmail(userEmail);
        if (existingAccount.isPresent()){
        	theModel.addAttribute("plannerUser", new PlannerUser());
			theModel.addAttribute("registrationError", "Email address already exists.");
        	return "/accounts/registration-form";
        }
            						
        accountService.save(thePlannerUser);
        
        return "/accounts/registration-confirmation";		
	}
}
