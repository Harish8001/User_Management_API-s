package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.UserRegistrationEntity;

public interface IUserRegistratinRepo extends JpaRepository<UserRegistrationEntity, Integer> {
	
	public  UserRegistrationEntity findByEmail(String email);

}
