package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fondo_salud", schema = "data2")
public class FondoSalud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fondo_salud")
    private String nombre;
}
