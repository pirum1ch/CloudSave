package ru.pirum1ch.cloudsave.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.pirum1ch.cloudsave.dto.requests.SignRequest;
import ru.pirum1ch.cloudsave.services.UserService;

@RestController
@RequestMapping("/user/")
public class UserController {

    UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("addNew")
    public ResponseEntity<?> addNewUser (@RequestBody @Valid SignRequest request) {
        return new ResponseEntity<>(userService.addNewUser(request.getEmail(),
                request.getLogin(), request.getName(), request.getSurname(), request.getPassword()), HttpStatus.OK);
    }
}
