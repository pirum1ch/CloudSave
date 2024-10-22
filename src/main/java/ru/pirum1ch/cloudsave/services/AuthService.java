package ru.pirum1ch.cloudsave.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pirum1ch.cloudsave.dto.requests.LoginRequest;
import ru.pirum1ch.cloudsave.dto.requests.SignRequest;
import ru.pirum1ch.cloudsave.dto.responces.TokenAuthResponce;
import ru.pirum1ch.cloudsave.models.User;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public TokenAuthResponce login(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails user = customUserDetailService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(user);
        return new TokenAuthResponce(token);
    }

    public TokenAuthResponce signUp (SignRequest request){

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        String token = jwtService.generateToken(user);
        return new TokenAuthResponce(token);
    }
}
