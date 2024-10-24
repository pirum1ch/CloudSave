package ru.pirum1ch.cloudsave.handlers;


import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
@Log4j2
public class ExceptionHandlerAdvice {


    @ExceptionHandler()
    public ResponseEntity badCredentialException (BadCredentialsException credentialsException){
       log.log(Level.ERROR, credentialsException.getLocalizedMessage());
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка ввода логина-пароля");
    }
}
