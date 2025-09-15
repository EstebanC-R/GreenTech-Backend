package com.api.cruds.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasAgendaDTO {
    private Long totalEventos;
    private Long eventosPendientes;
    private Long eventosCompletados;
    private Long eventosHoy;
}
