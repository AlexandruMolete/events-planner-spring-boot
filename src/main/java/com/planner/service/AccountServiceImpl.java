package com.planner.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planner.dao.AccountRepository;
import com.planner.dao.RoleRepository;
import com.planner.entity.Account;
import com.planner.entity.Role;
import com.planner.form.PlannerUser;

@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository accountRepository;
	
	private RoleRepository roleRepository;
	
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public AccountServiceImpl(AccountRepository theAccountRepository, RoleRepository theRoleRepository, BCryptPasswordEncoder thePasswordEncoder) {
		accountRepository = theAccountRepository;
		roleRepository = theRoleRepository;
		passwordEncoder = thePasswordEncoder;
	}
	
	@Override
	public List<Account> findAll(){
		
		return accountRepository.findAllByOrderByLastNameAsc();
		
	}

	@Override
	public Optional<Account> findById(int theId) {
		
		Optional<Account> result = accountRepository.findById(theId);
		return result;
	}

	@Override
	public Optional<Account> findByEmail(String theEmail) {
		
		return accountRepository.findFirstByEmail(theEmail);
	}

	@Override
	public void save(PlannerUser plannerUser) {
		Role defaultRole = roleRepository.findFirstByName("ROLE_GUEST").get();
		Account account = new Account(
				plannerUser.getEmail(),
				passwordEncoder.encode(plannerUser.getPassword()),
				plannerUser.getFirstName(),
				plannerUser.getLastName(),
				Arrays.asList(defaultRole)
				);

		accountRepository.save(account);
	}

	@Override
	public void deleteById(int theId) {
		accountRepository.deleteById(theId);
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> result = accountRepository.findFirstByEmail(username);
		if(result.isEmpty()) {
			throw new UsernameNotFoundException("Invalid email or password");
		}
		Account account = result.get();
		User user = new User(
				account.getEmail(),
				account.getPassword(),
				mapRolesToAuthorities(account.getRoles()));
		return user;
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream()
					.map(role -> new SimpleGrantedAuthority(role.getName()))
					.collect(Collectors.toList());
	}
}
