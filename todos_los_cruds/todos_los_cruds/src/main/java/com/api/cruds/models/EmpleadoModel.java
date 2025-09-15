package com.api.cruds.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "datos_empleados",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_empleado_cedula", columnNames = {"cedula"}),
                @UniqueConstraint(name = "uq_empleado_correo", columnNames = {"correo"}),
                @UniqueConstraint(name = "uq_empleado_celular", columnNames = {"celular"})
        }
)
public class EmpleadoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long id;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(length = 50, nullable = false)
    private String nombre;

    @Size(max = 50, message = "La asignación no puede exceder 50 caracteres")
    @Column(length = 50)
    private String asignacion;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(max = 10, message = "La cédula no puede exceder 10 caracteres")
    @Column(name = "cedula", length = 10, nullable = false, unique = true)
    private String cedula;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_de_ingreso")
    private LocalDate fechaDeIngreso;

    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 10, message = "El celular no puede exceder 10 caracteres")
    @Column(name = "celular", length = 10, nullable = false, unique = true)
    private String celular;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    @Column(name = "correo", length = 100, nullable = false, unique = true)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEmpleado estado = EstadoEmpleado.Activo;

    // Campos de auditoría
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Métodos de auditoría automática
    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (estado == null) {
            estado = EstadoEmpleado.Activo;
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}