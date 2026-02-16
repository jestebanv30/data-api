package com.data.datafacturador.sucursal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para datos de referencia
 * Usado para retornar id + nombre de tablas de referencia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaDTO {
    private Long id;
    private String nombre;
    private String codigo;
}
