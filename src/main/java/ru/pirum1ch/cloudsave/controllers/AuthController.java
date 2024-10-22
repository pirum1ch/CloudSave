package ru.pirum1ch.cloudsave.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pirum1ch.cloudsave.dto.requests.LoginRequest;
import ru.pirum1ch.cloudsave.dto.responces.TokenAuthResponce;
import ru.pirum1ch.cloudsave.models.User;
import ru.pirum1ch.cloudsave.services.AuthService;
import ru.pirum1ch.cloudsave.services.UserService;

@RestController
@RequestMapping("/")
@CrossOrigin
@Log4j2
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("login")
    public TokenAuthResponce login (@RequestBody @Valid LoginRequest request){
            return authService.login(request);
    }
}
