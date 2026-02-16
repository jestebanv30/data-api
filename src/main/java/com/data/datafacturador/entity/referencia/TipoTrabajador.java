package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_trabajador", schema = "data2")
public class TipoTrabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_dian")
    private String codigo;

    @Column(name = "tipo_trabajador")
    private String nombre;
}
