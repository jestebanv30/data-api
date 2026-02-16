package com.data.datafacturador.entity.nomina;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "banco", schema = "data2")
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_banco")
    private String nombre;

    @Column(name = "empresa_id")
    private Integer empresaId;
}
