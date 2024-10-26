package ru.pirum1ch.cloudsave.handlers;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

@RestControllerAdvice
@Log4j2
public class ExceptionHandlerAdvice {

    @ExceptionHandler()
    public ResponseEntity badCredentialException(BadCredentialsException credentialsException) {
        log.log(Level.ERROR, credentialsException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка ввода логина-пароля");
    }

    @ExceptionHandler()
    public ResponseEntity illegalArgumentException(IllegalArgumentException argumentException) {
        log.log(Level.ERROR, argumentException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка в запросе, проверьте правильность параметров.");
    }

    @ExceptionHandler()
    public ResponseEntity ioException(IOException ioException) {
        log.log(Level.ERROR, ioException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутрення ошибка сервера  Попробуйте еще раз.");
    }

    @ExceptionHandler()
    public ResponseEntity notFoundException(FileNotFoundException notFoundException) {
        log.log(Level.ERROR, notFoundException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка обработки файла");
    }

    @ExceptionHandler()
    public ResponseEntity malformedURLException(MalformedURLException malformedURLException) {
        log.log(Level.ERROR, malformedURLException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка обработки пути к файлу");
    }

    @ExceptionHandler()
    public ResponseEntity numberFormatException(NumberFormatException numberFormatException) {
        log.log(Level.ERROR, numberFormatException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка ввода целочисленного значения");
    }

//    @ExceptionHandler()
//    public ResponseEntity runtimeException(RuntimeException runtimeException) {
//        log.log(Level.ERROR, runtimeException.getLocalizedMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка выполенения");
//    }
}
