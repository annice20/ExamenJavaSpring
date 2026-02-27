package com.example.examen.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.examen.model.User;
import com.example.examen.repository.UserRepository;
import com.example.examen.service.EmailService;
import com.example.examen.service.OtpService;

@Controller
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder encoder;

    // ================= PAGE ACCUEIL =================
    @GetMapping("/")
    public String home(HttpSession session, Model model) {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", username);
        return "home";
    }

    // ================= INSCRIPTION =================
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {

        if (repo.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email déjà utilisé !");
            return "register";
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(false);
        user.setOtp(null);
        repo.save(user);

        model.addAttribute("message", "Inscription réussie !");
        return "index";
    }

    // ================= LOGIN =================
    @GetMapping("/login")
    public String loginPage() {
        return "index";
    }

    @PostMapping("/login")
    public String login(String email, String password, Model model) {

        User user = repo.findByEmail(email).orElse(null);

        if (user == null) {
            System.out.println("Utilisateur introuvable !");
            return "index";
        }

        if (user == null || !encoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "index";
        }

        String otp = otpService.generateOtp();

        user.setOtp(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(5));

        repo.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        model.addAttribute("email", email);
        return "verify-otp";
    }

    // ================= VERIFICATION OTP =================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email, 
                            @RequestParam("otp") String otp,
                            HttpSession session,
                            Model model) {

        User user = repo.findByEmail(email).orElse(null);

        // DEBUG: Affiche les valeurs pour identifier le coupable
        if (user != null) {
            System.out.println("OTP saisi: " + otp);
            System.out.println("OTP en base: " + user.getOtp());
            System.out.println("Expiré ? " + user.getOtpExpiration().isBefore(LocalDateTime.now()));
        }

        if (user == null ||
            user.getOtp() == null ||
            !otp.equals(user.getOtp()) || // <-- Changé ici si vous stockez en clair
            user.getOtpExpiration().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Code OTP invalide ou expiré !");
            model.addAttribute("email", email);
            return "verify-otp";
        }

        // Succès
        user.setEnabled(true);
        user.setOtp(null); 
        user.setOtpExpiration(null);
        repo.save(user);

        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}