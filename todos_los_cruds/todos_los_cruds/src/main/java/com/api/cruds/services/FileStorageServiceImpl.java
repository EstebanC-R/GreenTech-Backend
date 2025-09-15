package com.api.cruds.services;

import com.api.cruds.dto.FileInfoDto;
import com.api.cruds.exceptions.FileNotFoundException;
import com.api.cruds.models.EpsFile;
import com.api.cruds.models.StudiesFile;
import com.api.cruds.repositories.EpsFileRepository;
import com.api.cruds.repositories.FileStorageService;
import com.api.cruds.repositories.IEmpleadosRepository;
import com.api.cruds.repositories.StudiesFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final IEmpleadosRepository employeeRepository;
    private final EpsFileRepository epsFileRepository;
    private final StudiesFileRepository studiesFileRepository;

    @Override
    @Transactional
    public FileInfoDto uploadOrUpdateEps(Long employeeId, MultipartFile file) {
        validateEmployeeAndFile(employeeId, file);

        try {
            EpsFile entity = epsFileRepository.findByEmployeeId(employeeId)
                    .orElseGet(EpsFile::new);

            entity.setEmployeeId(employeeId);
            entity.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "eps-file");
            entity.setContentType(file.getContentType());
            entity.setSize(file.getSize());
            entity.setData(file.getBytes());

            EpsFile saved = epsFileRepository.save(entity);
            return FileInfoDto.builder()
                    .id(saved.getId())
                    .employeeId(employeeId)
                    .fileName(saved.getFileName())
                    .contentType(saved.getContentType())
                    .size(saved.getSize())
                    .updatedAt(saved.getUpdatedAt() != null ? saved.getUpdatedAt() : Instant.now())
                    .build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo", e);
        }
    }

    @Override
    @Transactional
    public FileInfoDto uploadOrUpdateStudies(Long employeeId, MultipartFile file) {
        validateEmployeeAndFile(employeeId, file);

        try {
            StudiesFile entity = studiesFileRepository.findByEmployeeId(employeeId)
                    .orElseGet(StudiesFile::new);

            entity.setEmployeeId(employeeId);
            entity.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "studies-file");
            entity.setContentType(file.getContentType());
            entity.setSize(file.getSize());
            entity.setData(file.getBytes());

            StudiesFile saved = studiesFileRepository.save(entity);
            return FileInfoDto.builder()
                    .id(saved.getId())
                    .employeeId(employeeId)
                    .fileName(saved.getFileName())
                    .contentType(saved.getContentType())
                    .size(saved.getSize())
                    .updatedAt(saved.getUpdatedAt() != null ? saved.getUpdatedAt() : Instant.now())
                    .build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileDownload getEps(Long employeeId) {
        EpsFile entity = epsFileRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new FileNotFoundException("Archivo EPS no encontrado para el empleado con ID: " + employeeId));
        return FileDownload.builder()
                .fileName(entity.getFileName())
                .contentType(defaultType(entity.getContentType()))
                .data(entity.getData())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FileDownload getStudies(Long employeeId) {
        StudiesFile entity = studiesFileRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new FileNotFoundException("Archivo de estudios no encontrado para el empleado con ID: " + employeeId));
        return FileDownload.builder()
                .fileName(entity.getFileName())
                .contentType(defaultType(entity.getContentType()))
                .data(entity.getData())
                .build();
    }

    private void validateEmployeeAndFile(Long employeeId, MultipartFile file) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no existe");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }
    }

    private String defaultType(String ct) {
        return (ct == null || ct.isBlank()) ? "application/octet-stream" : ct;
    }
}