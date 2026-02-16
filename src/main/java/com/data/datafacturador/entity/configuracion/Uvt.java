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

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "empresa_id")
    private Integer empresaId; // Null = Global
}
