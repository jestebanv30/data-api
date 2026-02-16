package com.data.datafacturador.entity.configuracion;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "impuesto", schema = "data2")
public class Impuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal porcentaje; // Numeric(10,2)

    @Column(name = "fecha_creacion")
    private ZonedDateTime fechaCreacion;

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
}
