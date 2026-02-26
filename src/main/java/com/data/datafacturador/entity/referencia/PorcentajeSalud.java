package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "porcentaje_salud", schema = "data2")
public class PorcentajeSalud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "porcentaje", nullable = false)
    private BigDecimal porcentaje;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "empresa_id")
    private Integer empresaId; // NULL = Global
}
