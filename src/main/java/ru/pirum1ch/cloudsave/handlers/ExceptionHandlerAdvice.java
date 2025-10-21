package ru.pirum1ch.cloudsave.handlers;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestControllerAdvice
@Log4j2
public class    ExceptionHandlerAdvice {

    @ExceptionHandler()
    public ResponseEntity<?> badCredentialException(BadCredentialsException credentialsException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка ввода логина-пароля: " + credentialsException.getLocalizedMessage());
    }

    @ExceptionHandler()
    public ResponseEntity<?> illegalArgumentException(IllegalArgumentException argumentException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка в запросе, проверьте правильность параметров.");
    }

    @ExceptionHandler()
    public ResponseEntity<?> ioException(IOException ioException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутрення ошибка сервера  Попробуйте еще раз.");
    }

    @ExceptionHandler()
    public ResponseEntity<?> notFoundException(FileNotFoundException fileNotFoundException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при обработке файла: такой файл не найден!");
    }

    @ExceptionHandler()
    public ResponseEntity<?> malformedURLException(MalformedURLException malformedURLException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка обработки пути к файлу");
    }

    @ExceptionHandler()
    public ResponseEntity<?> numberFormatException(NumberFormatException numberFormatException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка ввода целочисленного значения - " + numberFormatException.getLocalizedMessage());
    }

//    @ExceptionHandler()
//    public ResponseEntity runtimeException(RuntimeException runtimeException) {
//        log.log(Level.ERROR, runtimeException.getLocalizedMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка выполенения");
//    }
}
