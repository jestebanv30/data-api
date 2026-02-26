package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "centro_costo", schema = "data2")
public class CentroCosto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "centro_costo")
    private String nombre;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "empresa_id")
    private Integer empresaId;
}
