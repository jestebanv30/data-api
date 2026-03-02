package com.data.datafacturador.entity.facturacion;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "resolucion_tipo_documento", schema = "data2")
public class ResolucionTipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_tipo_documento", nullable = false)
    private Long idTipoDocumento; // 1=Factura, 2=Nota Crédito, 3=Nota Débito, etc.

    @Column(length = 10)
    private String prefijo;

    @Column(name = "numero_resolucion", length = 55)
    private String numeroResolucion;

    @Column(name = "rango_desde")
    private BigDecimal rangoDesde;

    @Column(name = "rango_hasta")
    private BigDecimal rangoHasta;

    @Column(name = "fecha_desde")
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDate fechaHasta;

    /** Contador de documentos emitidos bajo esta resolución (se resetea a 0 al cambiar resolución) */
    private BigDecimal correlativo;

    /** Contador de Notas Crédito emitidas con esta resolución */
    @Column(name = "correlativo_nc")
    private BigDecimal correlativoNc;

    /** Contador de Notas Débito emitidas con esta resolución */
    @Column(name = "correlativo_nd")
    private BigDecimal correlativoNd;

    @Column(name = "rango_alerta")
    private Long rangoAlerta; // Avisar cuando falten X números

    // --- Credenciales técnicas DIAN (solo SuperAdmin puede editarlas) ---

    @Column(name = "clave_tecnica", length = 255)
    private String claveTecnica;

    @Column(name = "testsetid", length = 255)
    private String testSetId;

    @Column(name = "pin", length = 55)
    private String pin;

    @Column(name = "id_software", length = 50)
    private String idSoftware;

    /**
     * Indica si la resolución está en modo pruebas (Set de Pruebas DIAN).
     * Valores: "SI" o "NO". Cuando está en "SI", los documentos van al ambiente de pruebas.
     * Solo SuperAdmin puede activar/desactivar.
     */
    @Column(name = "set_pruebas", length = 10)
    private String setPruebas; // "SI" o "NO"

    // --- Prefijos de Notas (Crédito y Débito) ---
    @Column(name = "prefijo_nc", length = 10)
    private String prefijoNc;

    @Column(name = "prefijo_nd", length = 10)
    private String prefijoNd;

    // --- Relaciones y auditoría ---
    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;

    @Column(name = "id_sucursal", nullable = false)
    private Long idSucursal;

    @Column(name = "empresa_id")
    private Integer empresaId;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        fechaActualizacion = ZonedDateTime.now();
    }
}
