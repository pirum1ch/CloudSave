package ru.pirum1ch.cloudsave.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pirum1ch.cloudsave.models.User;

import java.lang.annotation.Native;

@Repository
public interface UserRepo extends JpaRepository <User, Long> {
    @Query(nativeQuery = true, value = "SELECT id, email, login, name, password, surname FROM public.users where email=?1;")
    User findByEmail(String email);

    @Query(nativeQuery = true, value="SELECT id FROM users ORDER BY id DESC LIMIT 1")
    Long getLastIds();
}
