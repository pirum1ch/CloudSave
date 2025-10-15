package ru.pirum1ch.cloudsave.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.pirum1ch.cloudsave.models.File;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface FileRepo extends JpaRepository<File, Long> {

    @Override
    <S extends File> S save(S entity);

    File findByName(String name);

}
