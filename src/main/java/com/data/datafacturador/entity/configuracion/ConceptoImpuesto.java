package com.data.datafacturador.entity.configuracion;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "concepto_impuesto", schema = "data2")
public class ConceptoImpuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concepto_impuesto", length = 100)
    private String conceptoImpuesto;

    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "empresa_id")
    private Integer empresaId; // Nullable for Global
}
