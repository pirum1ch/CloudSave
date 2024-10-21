package ru.pirum1ch.cloudsave.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pirum1ch.cloudsave.models.User;
import ru.pirum1ch.cloudsave.services.UserService;

@RestController
@RequestMapping
@CrossOrigin
@Log4j2
public class AuthController {

    UserService userService;
    HttpServletRequest servletRequest;

    public AuthController( HttpServletRequest servletRequest) {
        this.userService = userService;
        this.servletRequest = servletRequest;
    }
    @PostMapping("/login")
    public ResponseEntity<User> login (String login, String password){
        String authTokem = servletRequest.getHeader("auth-token");
        System.out.println(authTokem);
       log.log(Level.INFO, "login: " + login + "password: " + password);
       if(userService.getUser(login, password) != null) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }
}
