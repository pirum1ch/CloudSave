package ru.pirum1ch.cloudsave.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pirum1ch.cloudsave.models.User;

@Repository
public interface UserRepo extends JpaRepository <User, Long> {
    User findByEmail(String email);
}
