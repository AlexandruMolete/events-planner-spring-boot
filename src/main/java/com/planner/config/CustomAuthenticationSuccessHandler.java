package com.planner.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.planner.entity.Account;
import com.planner.service.AccountService;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Lazy
	@Autowired
	private AccountService accountService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String accountEmail = authentication.getName();
		Account theAccount = accountService.findByEmail(accountEmail).get();
		HttpSession session = request.getSession();
		session.setAttribute("account", theAccount);
		
		response.sendRedirect(request.getContextPath() + "/");

	}

}
