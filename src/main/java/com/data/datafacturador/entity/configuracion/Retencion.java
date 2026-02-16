package com.data.datafacturador.entity.configuracion;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "retencion", schema = "data2")
public class Retencion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_retencion", length = 55)
    private String tipoRetencion; // "RETE FUENTE", etc.

    @Column(nullable = false)
    private BigDecimal porcentaje;

    @Column(length = 255)
    private String concepto;

    @Column(name = "base_uvt")
    private BigDecimal baseUvt;

    @Column(name = "fecha_creacion")
    private ZonedDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;

    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "empresa_id")
    private Integer empresaId; // Nullable for Global

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = ZonedDateTime.now();
    }
}
