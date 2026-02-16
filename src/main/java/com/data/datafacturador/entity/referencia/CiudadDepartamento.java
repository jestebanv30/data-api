package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad CiudadDepartamento
 * Tabla data2.ciudad_departamento
 */
@Entity
@Table(name = "ciudad_departamento", schema = "data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CiudadDepartamento {

    @Id
    private Long id; // No GeneratedValue as per user data (5001, 5002...)

    @Column(nullable = false)
    private String nombre;

    @Column(name = "id_departamento", nullable = false)
    private String idDepartamento;

    @Column(nullable = false)
    private String departamento;

    @Column(nullable = false)
    private String provincia;
}
