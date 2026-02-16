package com.data.datafacturador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoListaDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String cc;
    private CargoDTO cargo;
    private String estado;
    private String fechaInicio; // LocalDate as String for frontend
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CargoDTO {
        private String nombre;
    }
}
