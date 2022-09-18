package com.planner.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planner.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	
	public Optional<Role> findFirstByName(String roleName);
}
