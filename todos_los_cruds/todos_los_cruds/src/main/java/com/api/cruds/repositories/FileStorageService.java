package com.api.cruds.repositories;

import com.api.cruds.dto.FileInfoDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileInfoDto uploadOrUpdateEps(Long employeeId, MultipartFile file);
    FileInfoDto uploadOrUpdateStudies(Long employeeId, MultipartFile file);

    @Value
    @Builder
    @Jacksonized
    class FileDownload {
        String fileName;
        String contentType;
        byte[] data;
    }

    FileDownload getEps(Long employeeId);
    FileDownload getStudies(Long employeeId);
}
