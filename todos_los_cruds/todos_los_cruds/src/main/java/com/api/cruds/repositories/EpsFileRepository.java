package com.api.cruds.repositories;


import com.api.cruds.models.EpsFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EpsFileRepository extends JpaRepository<EpsFile, Long> {
    Optional<EpsFile> findByEmployeeId(Long employeeId);
}
