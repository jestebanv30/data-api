package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Pais
 * Tabla data2.pais
 */
@Entity
@Table(name = "pais", schema = "data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 2)
    private String codigo;

    @Column(length = 255)
    private String moneda;
}
