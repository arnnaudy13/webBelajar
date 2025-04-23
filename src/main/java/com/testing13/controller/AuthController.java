package com.testing13.controller;
import com.testing13.dto.VerifyRequest;
import com.testing13.config.JwtUtil;
import com.testing13.dto.LoginRequest;
import com.testing13.dto.RegisterRequest;
import com.testing13.entity.OtpToken;
import com.testing13.entity.Role;
import com.testing13.entity.User;
import com.testing13.repository.OtpTokenRepository;
import com.testing13.repository.RoleRepository;
import com.testing13.repository.UserRepository;
import com.testing13.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private OtpTokenRepository otpRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        Optional<User> existing = userRepo.findByEmailIgnoreCase(request.getEmail());
        if (existing.isPresent()) {
            return ResponseEntity.status(400).body("Email already registered.");
        }

        Role userRole = roleRepo.findByName(request.isAdmin() ? "ADMIN" : "STUDENT")
                .orElseGet(() -> roleRepo.save(Role.builder().name(request.isAdmin() ? "ADMIN" : "STUDENT").build()));

        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .roles(Set.of(userRole))
                .build();

        userRepo.save(newUser);

        // Generate OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        OtpToken token = OtpToken.builder()
                .email(request.getEmail())
                .otp(otpCode)
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        otpRepo.save(token);

        try {
            emailService.sendOtp(request.getEmail(), otpCode);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️ log the full error
            return ResponseEntity.status(500).body("Failed to send OTP email.");
        }

        return ResponseEntity.ok("Registration successful. Please check your email for OTP.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyRequest request) {
        Optional<OtpToken> tokenOpt = otpRepo.findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp());

        if (tokenOpt.isPresent()) {
            OtpToken token = tokenOpt.get();

            if (token.getExpirationTime().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("OTP expired.");
            }

            token.setUsed(true);
            otpRepo.save(token);

            User user = userRepo.findByEmailIgnoreCase(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setEnabled(true);
            userRepo.save(user);

            return ResponseEntity.ok("Account verified! You can now log in.");
        }

        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("Email received: " + request.getEmail());
        Optional<User> optionalUser = userRepo.findByEmailIgnoreCase(request.getEmail());
        System.out.println("User from repo: " + optionalUser.orElse(null));

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("User not found.");
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body("Account not verified. Please check your email.");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    )
            );

            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            String token = jwtUtil.generateToken(user.getEmail(), roleNames);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", user.getEmail(),
                    "roles", roleNames
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }
    }

}
