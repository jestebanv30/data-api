package com.data.datafacturador.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para response de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String username;
    private String email;
    private String nombre;
    private Long empresaId;
    private String empresaNombre;
    private List<String> roles;
    private Boolean activo;
    private String rol;
    private Long idEmpleado;
    private String empleadoNombre;
    private String empleadoIdentificacion;
    private List<Long> sucursalesIds;
    private List<String> permisos;
}
