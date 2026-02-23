package com.example.examen.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examen.model.User;
import com.example.examen.repository.UserRepository;
import com.example.examen.service.EmailService;
import com.example.examen.service.OtpService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private UserRepository repo;

	@Autowired
	private OtpService otpService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder encoder;
	
	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}

	@PostMapping("/register")
	public String register(@RequestBody User user){
	 user.setPassword(encoder.encode(user.getPassword()));	
	 repo.save(user);
	 return "Inscription OK";
	}

	@PostMapping("/login")
	public String login(@RequestBody Map<String,String> data){

	 //User user = repo.findByUsername(data.get("username")).orElse(null);
	 User user = repo.findByEmail(data.get("email")).orElse(null);

	 if(user==null || !encoder.matches(data.get("password"), user.getPassword())){
	   return "Identifiants incorrects";
	 }

	 String otp = otpService.generateOtp();
	 user.setOtp(otp);
	 user.setOtpExpiration(LocalDateTime.now().plusMinutes(5));
	 repo.save(user);

	 emailService.sendOtp(user.getEmail(), otp);

	 return "OTP envoyé";
	}

	@PostMapping("/verify-otp")
	public String verify(@RequestBody Map<String,String> data){

	 User user = repo.findByUsername(data.get("username")).orElse(null);

	 if(user.getOtp().equals(data.get("otp")) &&
	    user.getOtpExpiration().isAfter(LocalDateTime.now())){

	   user.setEnabled(true);
	   user.setOtp(null);
	   repo.save(user);

	   return "Connexion réussie";
	 }

	 return "OTP invalide";
	}
}
