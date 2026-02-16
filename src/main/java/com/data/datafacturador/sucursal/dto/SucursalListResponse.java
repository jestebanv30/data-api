package com.data.datafacturador.sucursal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO resumido para listar sucursales
 * Solo campos esenciales para mostrar en lista
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalListResponse {

    private Long id;
    private Integer empresaId; // Para identificar a qué empresa pertenece (Admin)
    private String nombreComercial;
    private String nombreRazonSocial;
    private String ccNit;
    private String ciudad;
    private String departamento;
    private String estado;
    private Boolean activa;
}
