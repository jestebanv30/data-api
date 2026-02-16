package com.data.datafacturador.entity.nomina;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "forma_pago", schema = "data2")
public class FormaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_forma_pago")
    private String nombre;
    
    @Column(name = "codigo_dian")
    private String codigo;

    @Column(name = "empresa_id")
    private Integer empresaId;
}
