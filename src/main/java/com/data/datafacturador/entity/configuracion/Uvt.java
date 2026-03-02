package com.data.datafacturador.entity.configuracion;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "uvt", schema = "data2")
public class Uvt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "valor_uvt", nullable = false)
    private BigDecimal valorUvt;

    @Column(name = "tope_uvt")
    private BigDecimal topeUvt;

    @Column(name = "fecha_creacion")
    private java.time.ZonedDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = java.time.ZonedDateTime.now();
    }
}
