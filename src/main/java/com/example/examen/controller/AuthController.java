package com.example.examen.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.examen.model.Role;
import com.example.examen.model.User;
import com.example.examen.repository.UserRepository;
import com.example.examen.service.EmailService;
import com.example.examen.service.OtpService;
import com.example.examen.service.TotpService;

@Controller
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private OtpService otpService;
    
    @Autowired 
    private TotpService totpService;

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

        if (user != null) {
            System.out.println("OTP saisi: " + otp);
            System.out.println("OTP en base: " + user.getOtp());
            System.out.println("Expiré ? " + user.getOtpExpiration().isBefore(LocalDateTime.now()));
        }

        if (user == null ||
            user.getOtp() == null ||
            !otp.equals(user.getOtp()) ||
            user.getOtpExpiration().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Code OTP invalide ou expiré !");
            model.addAttribute("email", email);
            return "verify-otp";
        }

        user.setEnabled(true);
        user.setOtp(null); 
        user.setOtpExpiration(null);
        repo.save(user);

     // Si 2FA NON activé → setup QR code
        if (!user.isTwoFactorEnabled()) {
            session.setAttribute("pending2faEmail", user.getEmail());
            return "redirect:/setup-2fa";
        }

        // Sinon → login normal
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        return switch (user.getRole()) {
            case ADMINISTRATEUR, SUPERVISEUR -> "redirect:/dashboard";
            case AGENT_ENREGISTREMENT        -> "redirect:/citoyens/liste";
            case AGENT_VALIDATION            -> "redirect:/citoyens/liste";
        };
    }
    
 // ========== LOGIN VIA GOOGLE AUTHENTICATOR ==========
    @PostMapping("/login-2fa")
    public String loginVia2fa(
            @RequestParam String email,
            @RequestParam String code,
            HttpSession session,
            Model model) {

        User user = repo.findByEmail(email).orElse(null);

        if (user == null || !user.isTwoFactorEnabled()) {
            model.addAttribute("error", "Aucun compte Authenticator trouvé pour cet email.");
            return "index";
        }

        if (!totpService.verifyCode(user.getSecret2FA(), code)) {
            model.addAttribute("error", "Code Authenticator invalide !");
            return "index";
        }

        // Création de la session
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("userId", user.getId());

        // Redirection par rôle
        return switch (user.getRole()) {
            case ADMINISTRATEUR, SUPERVISEUR -> "redirect:/dashboard";
            case AGENT_ENREGISTREMENT        -> "redirect:/citoyens/liste";
            case AGENT_VALIDATION            -> "redirect:/citoyens/liste";
        };
    }
    
    // ========== SETUP 2FA (QR Code) ==========
    @GetMapping("/setup-2fa")
    public String setup2faPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("pending2faEmail");
        if (email == null) return "redirect:/login";

        User user = repo.findByEmail(email).orElse(null);
        if (user == null) return "redirect:/login";

        try {
        	String secret = (String) session.getAttribute("temp2faSecret");
        	if (secret == null) {
        	    secret = totpService.generateSecret();
        	    session.setAttribute("temp2faSecret", secret);
        	}
            String qrUri = totpService.generateQrCodeDataUri(secret, email);
            model.addAttribute("qrUri", qrUri);
            model.addAttribute("secret", secret);
            model.addAttribute("email", email);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur génération QR Code");
        }

        return "setup-2fa";
    }

    @PostMapping("/setup-2fa")
    public String confirmSetup2fa(
            @RequestParam String code,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("pending2faEmail");
        String secret = (String) session.getAttribute("temp2faSecret");

        if (email == null || secret == null) return "redirect:/login";

        if (!totpService.verifyCode(secret, code)) {
            model.addAttribute("error", "Code invalide. Scannez à nouveau le QR Code.");
            try {
                model.addAttribute("qrUri", totpService.generateQrCodeDataUri(secret, email));
            } catch (Exception ignored) {}
            model.addAttribute("secret", secret);
            model.addAttribute("email", email);
            return "setup-2fa";
        }

        User user = repo.findByEmail(email).orElseThrow();
        user.setSecret2FA(secret);
        user.setTwoFactorEnabled(true);
        repo.save(user);

        session.removeAttribute("temp2faSecret");
        session.removeAttribute("pending2faEmail");

        // Création session
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("userId", user.getId());
        return redirectByRole(user.getRole());
    }
    
    @PostMapping("/verify-2fa")
    public String verify2fa(
            @RequestParam String code,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("pending2faEmail");
        if (email == null) return "redirect:/login";

        User user = repo.findByEmail(email).orElse(null);
        if (user == null) return "redirect:/login";

        if (!totpService.verifyCode(user.getSecret2FA(), code)) {
            model.addAttribute("error", "Code TOTP invalide !");
            model.addAttribute("email", email);
            return "verify-2fa";
        }

        session.removeAttribute("pending2faEmail");
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        // Redirection par rôle
        return switch (user.getRole()) {
            case ADMINISTRATEUR, SUPERVISEUR -> "redirect:/dashboard";
            case AGENT_ENREGISTREMENT        -> "redirect:/citoyens/liste";
            case AGENT_VALIDATION            -> "redirect:/citoyens/liste";
        };
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    private String redirectByRole(Role role) {
        if (role == null) return "redirect:/dashboard";
        return switch (role) {
            case ADMINISTRATEUR -> "redirect:/dashboard";
            case SUPERVISEUR -> "redirect:/dashboard";
            case AGENT_ENREGISTREMENT -> "redirect:/citoyens/liste";
            case AGENT_VALIDATION -> "redirect:/citoyens/liste";
        };
    }
}