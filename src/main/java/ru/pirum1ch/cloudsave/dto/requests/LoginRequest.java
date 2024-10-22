package ru.pirum1ch.cloudsave.dto.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@RequiredArgsConstructor
public class LoginRequest {

    String email;
    String password;
}
