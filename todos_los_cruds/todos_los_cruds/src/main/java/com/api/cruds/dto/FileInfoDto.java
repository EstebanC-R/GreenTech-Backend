package com.api.cruds.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder
@Jacksonized
public class FileInfoDto {
    Long id;
    Long employeeId;
    String fileName;
    String contentType;
    long size;
    Instant updatedAt;
}