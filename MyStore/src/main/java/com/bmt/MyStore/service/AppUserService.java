package com.bmt.MyStore.service;

import com.bmt.MyStore.models.AppUser;
import com.bmt.MyStore.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Injecting PasswordEncoder directly

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = repo.findByEmail(email);
        if (appUser != null) {
            return User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    public void saveUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encode the password before saving
        repo.save(user);
    }

    public boolean isEmailAlreadyInUse(String email) {
        return repo.findByEmail(email) != null;
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
