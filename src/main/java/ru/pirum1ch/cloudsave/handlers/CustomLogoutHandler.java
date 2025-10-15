package ru.pirum1ch.cloudsave.handlers;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.pirum1ch.cloudsave.models.Token;
import ru.pirum1ch.cloudsave.repositories.TokenRepo;

@Component
@Log4j2
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepo tokenRepo;
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Auth-Token";

    public CustomLogoutHandler(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        log.info("Выполняем логаут");
        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            return;
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        String token = authHeader.substring(BEARER_PREFIX.length());

        Token storedToken = tokenRepo.getToken(token);
        if (storedToken != null) {
            storedToken.setActive(false);
            tokenRepo.save(storedToken);
        } else {
            throw new JwtException("Нет такого токена в БД: " + token);
        }
    }
}
