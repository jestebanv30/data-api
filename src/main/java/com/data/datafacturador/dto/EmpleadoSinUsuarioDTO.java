package com.data.datafacturador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoSinUsuarioDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String cc;
}
