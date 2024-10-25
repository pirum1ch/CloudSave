package ru.pirum1ch.cloudsave.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pirum1ch.cloudsave.models.Token;

@Repository
public interface TokenRepo extends JpaRepository <Token, Long> {


    @Query(value = "select id, 'create_date', token, 'is-active'from Token")
    Token findAllByToken(String token);
}
