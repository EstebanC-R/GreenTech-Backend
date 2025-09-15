package com.api.cruds.repositories;


import com.api.cruds.models.StudiesFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudiesFileRepository extends JpaRepository<StudiesFile, Long> {
    Optional<StudiesFile> findByEmployeeId(Long employeeId);
}