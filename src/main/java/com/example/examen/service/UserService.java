package com.example.examen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.examen.model.User;
import com.example.examen.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder encoder;

	public User register(User u){
		u.setPassword(encoder.encode(u.getPassword()));
		u.setEnabled(false);
		return repo.save(u);
	}
}
