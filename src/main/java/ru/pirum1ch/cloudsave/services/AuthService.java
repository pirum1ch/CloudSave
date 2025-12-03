package ru.pirum1ch.cloudsave.services;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pirum1ch.cloudsave.dto.UserDto;
import ru.pirum1ch.cloudsave.dto.requests.LoginRequest;
import ru.pirum1ch.cloudsave.dto.requests.SignRequest;
import ru.pirum1ch.cloudsave.dto.responces.TokenAuthResponce;
import ru.pirum1ch.cloudsave.models.Token;
import ru.pirum1ch.cloudsave.models.User;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public TokenAuthResponce login(LoginRequest request) throws BadCredentialsException, UsernameNotFoundException {
        String token;

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        UserDetails user = customUserDetailService.loadUserByUsername(request.getLogin());
//        Token tokenEntity = jwtService.getActualToken(request.getLogin());

        //Получаем последний актуальный токен (в теории он может быть только один)
        token = jwtService.getActualToken(request.getLogin());

        //TODO take away to new method
        //Если токена нет
        if (token == null) {
            //выпускаем новый
            token = jwtService.generateToken(user);
            //И сохраняем его в БД
            jwtService.storeToken(token, request.getLogin());
        }else {
            //Проверяем не закончен ли его срок действия
            try {
                jwtService.isTokenExpired(token);
                //Если ловим ошибку по сроку действия
            } catch (ExpiredJwtException expiredJwtException) {
                //Помечаем его просроченным
                jwtService.setTokenDead(token);
                //Выпускаем новый токен
                token = jwtService.generateToken(user);
                //Сохраняем его в БД как действующий
                jwtService.storeToken(token, request.getLogin());
            }
        }
        return new TokenAuthResponce(token);
    }

    public User signUp(SignRequest request){
        User newUser = null;
        try {
            newUser = userService.addNewUser(request.getEmail(), request.getLogin(), request.getName(), request.getSurname(), request.getPassword());
            String token = jwtService.generateToken(userService.getUser(newUser.getEmail()));
            jwtService.storeToken(token, request.getEmail());
        }catch (IllegalArgumentException | NullPointerException exception){
            exception.fillInStackTrace();
        }
        return newUser;
    }

    public TokenAuthResponce logout(LoginRequest request){
        String token = jwtService.setTokenDead(request.getLogin());
        return new TokenAuthResponce(token);
    }
}
