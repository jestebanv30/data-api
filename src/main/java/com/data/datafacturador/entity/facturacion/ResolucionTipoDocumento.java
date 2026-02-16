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
    private Long idTipoDocumento; // 1=Factura, 2=POS, 3=Nota Crédito, etc.

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

    private BigDecimal correlativo; // Último usado

    @Column(name = "rango_alerta")
    private Long rangoAlerta; // Avisar cuando falten X números

    @Column(name = "clave_tecnica", length = 255) // Ampliado por seguridad, DB dice 55 pero claves técnicas suelen ser largas HEX
    private String claveTecnica;

    @Column(name = "testsetid", length = 255)
    private String testSetId;

    @Column(name = "pin", length = 55)
    private String pin;
    
    @Column(name = "id_software", length = 50)
    private String idSoftware;

    // --- Notas Crédito / Débito (Opcional en misma tabla según schema original) ---
    @Column(name = "prefijo_nc", length = 10)
    private String prefijoNc;
    
    @Column(name = "prefijo_nd", length = 10)
    private String prefijoNd;

    // --- Auditoría y Relaciones ---

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;
    
    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "id_usuario")
    private Long idUsuario; // Usuario que creó/modificó

    @Column(name = "empresa_id")
    private Integer empresaId;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (fechaActualizacion == null) fechaActualizacion = ZonedDateTime.now();
    }
}
