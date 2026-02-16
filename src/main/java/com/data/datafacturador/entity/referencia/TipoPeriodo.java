package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_periodo", schema = "data2")
public class TipoPeriodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_periodo")
    private String nombre;
}
