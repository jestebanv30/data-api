package com.data.datafacturador.sucursal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para Tercero
 * Usado en referencias (e.g. tercero por defecto)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerceroSimpleDTO {
    private Long id;
    private String nombreRazonSocial;
    private String ccNit;
    private String dv;
}
