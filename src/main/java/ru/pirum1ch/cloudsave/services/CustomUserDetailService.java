package ru.pirum1ch.cloudsave.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.pirum1ch.cloudsave.models.User;
import ru.pirum1ch.cloudsave.repositories.UserRepo;

@Service
public class CustomUserDetailService implements UserDetailsService {

    public CustomUserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user!=null) return user;
        else throw new UsernameNotFoundException("No suchUser");
    }
}
