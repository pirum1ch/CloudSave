package ru.pirum1ch.cloudsave.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.pirum1ch.cloudsave.dto.requests.LoginRequest;
import ru.pirum1ch.cloudsave.dto.requests.SignRequest;
import ru.pirum1ch.cloudsave.dto.responces.TokenAuthResponce;
import ru.pirum1ch.cloudsave.models.Token;
import ru.pirum1ch.cloudsave.services.AuthService;
import ru.pirum1ch.cloudsave.services.JwtService;

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
    public ResponseEntity<TokenAuthResponce> login(@RequestBody @Valid LoginRequest request) {
        log.info( "Попытка авторизации пользователя " + request.getLogin());
        return new ResponseEntity<> (authService.login(request), HttpStatus.OK);
    }

    @PostMapping("logout")
    public ResponseEntity<TokenAuthResponce> logout() {
//        return new ResponseEntity<> (authService.logout(request), HttpStatus.OK);
    return null;
    }

    @PostMapping("sign-up")
    public TokenAuthResponce signUp(@RequestBody @Valid SignRequest request) {
        return authService.signUp(request);
    }
}
