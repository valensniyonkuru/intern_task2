package com.bmt.MyStore.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bmt.MyStore.models.AppUser;
import com.bmt.MyStore.models.RegisterDto;
import com.bmt.MyStore.repositories.AppUserRepository;
import com.bmt.MyStore.service.AppUserService;

import jakarta.validation.Valid;

@Controller
public class AccountController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository repo;  // Inject AppUserRepository

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        model.addAttribute("success", false);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerDto") @Valid RegisterDto registerDto, BindingResult result, Model model) {

        // Check for password mismatch
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.addError(
                new FieldError("registerDto", "confirmPassword", "Password and Confirm Password do not match")
            );
        }

        // Check if email is already in use
        AppUser existingUser = repo.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            result.addError(
                new FieldError("registerDto", "email", "Email address is already used")
            );
        }

        // Return if there are validation errors
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Create a new account
            var bCryptEncoder = new BCryptPasswordEncoder();

            AppUser newUser = new AppUser();
            newUser.setFirstName(registerDto.getFirstName());
            newUser.setLastName(registerDto.getLastName());
            newUser.setEmail(registerDto.getEmail());
            newUser.setPhone(registerDto.getPhone());
            newUser.setAddress(registerDto.getAddress());
            newUser.setRole("client");
            newUser.setCreatedAt(new Date());
            newUser.setPassword(bCryptEncoder.encode(registerDto.getPassword()));

            // Save new user to the database
            repo.save(newUser);
            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);

        } catch (Exception ex) {
            result.addError(new FieldError("registerDto", "firstName", ex.getMessage()));
        }
        
        return "register";
    }
}
