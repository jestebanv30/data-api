package com.data.datafacturador.entity.nomina;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "medio_pago", schema = "data2")
public class MedioPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medio")
    private String nombre;
    
    @Column(name = "codigo")
    private String codigo;

    @Column(name = "empresa_id")
    private Integer empresaId;
}
