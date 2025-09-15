package com.api.cruds.dto;

import com.api.cruds.Configuration.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaEventoDTO {

    private Long idEvento;
    private String propietario;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @NotBlank(message = "El título es obligatorio", groups = {Create.class})
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha del evento es obligatoria", groups = {Create.class})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime fechaEvento;

    private String tipo = "OTRO";
    private Boolean completado = false;

    public interface Create {}
    public interface Update {}
}