package com.data.datafacturador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "El nombre de usuario/email es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Integer empresaId;

    @NotBlank(message = "El rol es obligatorio (administrador, empleado)")
    private String rol;

    private Long idEmpleado;

    private java.util.List<Long> sucursalesIds;
    private java.util.List<String> permisos;

    @Builder.Default
    private Boolean activo = true;
}
