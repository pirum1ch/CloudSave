package ru.pirum1ch.cloudsave.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pirum1ch.cloudsave.models.Token;

@Repository
public interface TokenRepo extends JpaRepository <Token, Long> {

    @Query(value = "select isActive from Token where token = :token")
    boolean getTokenStatus(String token);

    @Query(value = "select id, date, token, isActive, login from Token where token = :token")
    Token getToken(String token);

    @Query(value = "select token from Token where login = :login and isActive = true")
    String getActualToken (String login);
}
