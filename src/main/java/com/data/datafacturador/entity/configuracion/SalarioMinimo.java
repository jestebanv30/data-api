package com.data.datafacturador.entity.configuracion;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "salario", schema = "data2")
public class SalarioMinimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "salario_minimo", nullable = false)
    private BigDecimal salarioMinimo;

    @Column(name = "auxilio_transporte", nullable = false)
    private BigDecimal auxilioTransporte;

    @Column(name = "tope_salario_auxilio")
    private BigDecimal topeSalarioAuxilio;

    @Column(name = "fecha_creacion")
    private java.time.ZonedDateTime fechaCreacion;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "empresa_id")
    private Integer empresaId; // Null = Global

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = java.time.ZonedDateTime.now();
    }
}
