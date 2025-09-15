package com.api.cruds.controllers;

import com.api.cruds.dto.FileInfoDto;
import com.api.cruds.repositories.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/archivos")
@RequiredArgsConstructor
public class EmployeeFilesController {

    private final FileStorageService service;

    @PostMapping(path = "/{employeeId}/eps", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileInfoDto uploadOrUpdateEps(@PathVariable Long employeeId,
                                         @RequestParam("file") MultipartFile file) {
        return service.uploadOrUpdateEps(employeeId, file);
    }

    @GetMapping("/{employeeId}/eps")
    public ResponseEntity<byte[]> downloadEps(@PathVariable Long employeeId) {
        var f = service.getEps(employeeId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(f.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(f.getFileName(), StandardCharsets.UTF_8)
                .build());
        headers.setContentLength(f.getData().length);
        return ResponseEntity.ok().headers(headers).body(f.getData());
    }

    @PostMapping(path = "/{employeeId}/studies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileInfoDto uploadOrUpdateStudies(@PathVariable Long employeeId,
                                             @RequestParam("file") MultipartFile file) {
        return service.uploadOrUpdateStudies(employeeId, file);
    }

    @GetMapping("/{employeeId}/studies")
    public ResponseEntity<byte[]> downloadStudies(@PathVariable Long employeeId) {
        var f = service.getStudies(employeeId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(f.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(f.getFileName(), StandardCharsets.UTF_8)
                .build());
        headers.setContentLength(f.getData().length);
        return ResponseEntity.ok().headers(headers).body(f.getData());
    }
}
