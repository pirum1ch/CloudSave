package ru.pirum1ch.cloudsave.services;

import org.springframework.stereotype.Service;
import ru.pirum1ch.cloudsave.models.User;
import ru.pirum1ch.cloudsave.repositories.UserRepo;

@Service
public class UserService {

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    UserRepo userRepo;

    public User getUser(String login, String password){
        return userRepo.findByEmail(login);
    }
}
