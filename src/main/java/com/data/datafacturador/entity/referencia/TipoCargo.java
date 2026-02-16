package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_cargo", schema = "data2")
public class TipoCargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_cargo")
    private String nombre;
}
