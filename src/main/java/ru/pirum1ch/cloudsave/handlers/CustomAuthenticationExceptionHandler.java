package ru.pirum1ch.cloudsave.handlers;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Log4j2
public class CustomAuthenticationExceptionHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws
            IOException,
            JwtException
    {
        log.log(Level.INFO, "Ошибка авторизации : {}", e.getLocalizedMessage());
        httpServletResponse.setStatus(401);
        httpServletResponse.getWriter().write(e.getLocalizedMessage());
        httpServletResponse.getWriter().flush();
    }
}
