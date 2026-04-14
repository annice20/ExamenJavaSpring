package com.example.examen.controller.api;

import com.example.examen.model.User;
import com.example.examen.repository.UserRepository;
import com.example.examen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthApiController {

    @Autowired private UserRepository repo;
    @Autowired private OtpService otpService;
    @Autowired private TotpService totpService;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email déjà utilisé !"));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(false);
        repo.save(user);
        return ResponseEntity.ok(Map.of("message", "Inscription réussie !"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = repo.findByEmail(email).orElse(null);

        if (user == null || !encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect"));
        }

        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(5));
        repo.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        return ResponseEntity.ok(Map.of("message", "OTP envoyé par email", "email", email));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String otp = data.get("otp");

        User user = repo.findByEmail(email).orElse(null);

        if (user == null || user.getOtp() == null || !otp.equals(user.getOtp()) || 
            user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(401).body(Map.of("message", "Code OTP invalide ou expiré"));
        }

        user.setEnabled(true);
        user.setOtp(null);
        repo.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("needs2FA", !user.isTwoFactorEnabled());
        response.put("userId", user.getId());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2fa(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String code = data.get("code");

        User user = repo.findByEmail(email).orElse(null);

        if (user == null || !totpService.verifyCode(user.getSecret2FA(), code)) {
            return ResponseEntity.status(401).body(Map.of("message", "Code Authenticator invalide"));
        }

        return ResponseEntity.ok(Map.of(
            "userId", user.getId(),
            "username", user.getUsername(),
            "role", user.getRole()
        ));
    }
}