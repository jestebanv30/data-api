package com.data.datafacturador.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para datos de empresa
 * Incluye TODOS los campos de la tabla empresas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {

    private Long id;
    private String nombre; // Nombre comercial
    private String razonSocial; // Razón social
    private String nit; // NIT o documento de identificación
    private String direccion;
    private String ciudad;
    private String departamento;
    private String pais;
    private String telefono;
    private String celular;
    private String email;
    private String sitioWeb;
    private String logoUrl;
    private Boolean datosPrecargados;
    private java.util.Map<String, Object> configuracion; // JSON con configuraciones adicionales
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer creadoPor;
    private Integer actualizadoPor;
    private String estado; // ACTIVO, INACTIVO
    private Boolean activo; // Derivado de estado
}
