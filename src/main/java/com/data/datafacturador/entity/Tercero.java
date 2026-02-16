package com.data.datafacturador.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Entidad Tercero (Cliente/Proveedor)
 * Tabla data2.tercero
 */
@Entity
@Table(name = "tercero", schema = "data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tercero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @Column(name = "nombre_razon_social")
    private String nombreRazonSocial;

    @Column(name = "cc_nit")
    private String ccNit;

    @Column(name = "dv")
    private String dv;

    @Column(name = "tipo_tercero")
    private String tipoTercero;
    
    @Column(length = 255)
    private String direccion;
    
    @Column(length = 55)
    private String ciudad;
    
    @Column(length = 55)
    private String departamento;
    
    private String estado;
}
