package ru.pirum1ch.cloudsave.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.pirum1ch.cloudsave.models.Token;
import ru.pirum1ch.cloudsave.models.User;
import ru.pirum1ch.cloudsave.repositories.TokenRepo;
import ru.pirum1ch.cloudsave.repositories.UserRepo;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private final TokenRepo tokenRepo;
    private final UserRepo userRepo;
    public JwtService(TokenRepo tokenRepo, UserRepo userRepo) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
    }

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    //В данной конфигурации токен живет 10 минут
    @Value("${token.time.to.live}")
    private int tokenTimeToLive = 600000;


    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    public String extractUserName(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("email", customUserDetails.getEmail());
            claims.put("password", customUserDetails.getPassword());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return токен
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenTimeToLive))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Token storeToken (String token, String login){
        Token tokenEntity = Token.builder()
                .token(token)
                .date(new Date())
                .login(userRepo.findByEmail(login).getEmail())
                .isActive(true)
                .build();
        tokenRepo.save(tokenEntity);
        return tokenEntity;
    }

    public boolean isTokenDead (String token){
        return tokenRepo.getTokenStatus(token);
    }

    public String setTokenDead (String token){
        Token tokenEntity = tokenRepo.getToken(token);
        tokenEntity.setActive(false);
        tokenRepo.save(tokenEntity);
        return tokenEntity.getToken();
    }

    public String getActualToken (String login){
        return tokenRepo.getActualToken(login);
    }
}
