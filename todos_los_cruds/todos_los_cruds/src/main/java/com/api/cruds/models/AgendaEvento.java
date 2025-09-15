package com.api.cruds.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "agenda_eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoEvento tipo = TipoEvento.OTRO;

    @Column(name = "completado")
    private Boolean completado = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enum para tipos de evento
    public enum TipoEvento {
        RIEGO, FERTILIZACION, COSECHA, MANTENIMIENTO, REUNION, OTRO
    }
}