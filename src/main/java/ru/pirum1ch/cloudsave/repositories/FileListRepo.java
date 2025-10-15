package ru.pirum1ch.cloudsave.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.pirum1ch.cloudsave.models.File;
import org.springframework.data.domain.Pageable;

import java.util.Date;

@Repository
public interface FileListRepo extends PagingAndSortingRepository<File, Long> {
    Page<File> findAll(Pageable pageable);
}
