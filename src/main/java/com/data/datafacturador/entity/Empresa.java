package com.data.datafacturador.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Empresa - Representa una empresa en el sistema multiempresa
 * Mapea TODOS los campos de la tabla data2.empresas
 */
@Entity
@Table(name = "empresas", schema = "data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre; // Nombre comercial

    @Column(name = "razon_social", nullable = false, length = 255)
    private String razonSocial;

    @Column(nullable = false, unique = true, length = 50)
    private String nit;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String departamento;

    @Column(length = 100)
    private String pais;

    @Column(length = 50)
    private String telefono;

    @Column(length = 50)
    private String celular;

    @Column(length = 100)
    private String email;

    @Column(name = "sitio_web", length = 255)
    private String sitioWeb;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "datos_precargados")
    private Boolean datosPrecargados = false;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "configuracion", columnDefinition = "jsonb")
    private java.util.Map<String, Object> configuracion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(length = 20, nullable = false)
    private String estado = "ACTIVO";

    // Métodos de utilidad
    public boolean isActiva() {
        return "ACTIVO".equals(this.estado);
    }

    public Boolean getActivo() {
        return isActiva();
    }
}
