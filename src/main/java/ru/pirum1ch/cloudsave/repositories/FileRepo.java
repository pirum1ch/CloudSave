package ru.pirum1ch.cloudsave.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pirum1ch.cloudsave.models.File;

import java.util.List;

@Repository
public interface FileRepo extends JpaRepository<File, Long> {

    @Override
    <S extends File> S save(S entity);

    File findByName(String name);

    List<File> findAll();

}
