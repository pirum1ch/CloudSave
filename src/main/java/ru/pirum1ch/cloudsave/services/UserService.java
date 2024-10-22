package ru.pirum1ch.cloudsave.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pirum1ch.cloudsave.models.User;
import ru.pirum1ch.cloudsave.repositories.UserRepo;

@Service
@RequiredArgsConstructor
public class UserService {

    UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getUser(String login, String password){
        return userRepo.findByEmail(login);
    }
}
