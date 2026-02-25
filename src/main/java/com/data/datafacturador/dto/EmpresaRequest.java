package com.data.datafacturador.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@lombok.experimental.SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    @NotBlank(message = "El NIT es obligatorio")
    private String nit;

    private String dv; // Dígito de verificación (opcional)

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
    private java.util.Map<String, Object> configuracion; // JSON
}
