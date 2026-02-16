package com.data.datafacturador.sucursal.entity;

import com.data.datafacturador.entity.referencia.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Entidad Sucursal - Representa una sede/sucursal de una empresa
 * Contiene todos los datos operativos detallados
 */
@Entity
@Table(name = "sucursal", schema = "data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @Column(name = "nombre_razon_social", length = 255)
    private String nombreRazonSocial;

    @Column(name = "nombre_comercial", length = 255)
    private String nombreComercial;

    @Column(name = "cc_nit", length = 55)
    private String ccNit;

    @Column(name = "dv", length = 55)
    private String dv; // Dígito de verificación

    @Column(name = "id_tipo_organizacion")
    private Long idTipoOrganizacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_organizacion", insertable = false, updatable = false)
    private TipoOrganizacion tipoOrganizacion;

    @Column(name = "id_tipo_identificacion")
    private Long idTipoIdentificacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_identificacion", insertable = false, updatable = false)
    private TipoIdentificacion tipoIdentificacion;

    @Column(name = "id_responsabilidad_fiscal")
    private Long idResponsabilidadFiscal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_responsabilidad_fiscal", insertable = false, updatable = false)
    private ResponsabilidadFiscal responsabilidadFiscal;

    @Column(name = "id_tipo_regimen")
    private Long idTipoRegimen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_regimen", insertable = false, updatable = false)
    private TipoRegimen tipoRegimen;

    @Column(name = "id_tipo_responsabilidad_tributaria")
    private Long idTipoResponsabilidadTributaria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_responsabilidad_tributaria", insertable = false, updatable = false)
    private ResponsabilidadTributaria responsabilidadTributaria;

    @Column(length = 55)
    private String pais;

    @Column(name = "id_ciudad", length = 55)
    private String idCiudad;

    @Column(length = 55)
    private String ciudad;

    @Column(length = 55)
    private String departamento;

    @Column(length = 255)
    private String direccion;

    @Column(length = 55)
    private String celular;

    @Column(length = 55)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(length = 500)
    private String slogan; // Pie de página factura

    @Column(name = "slogan_orden", length = 1500)
    private String sloganOrden; // Pie de página orden de servicio

    @Column(name = "copias_ventas")
    private Long copiasVentas;

    @Column(name = "copias_compras")
    private Long copiasCompras;

    @Column(name = "copias_cobros")
    private Long copiasCobros;

    @Column(name = "copias_pagos")
    private Long copiasPagos;

    @Column(columnDefinition = "bytea")
    private byte[] logo;

    @Column(length = 20)
    private String estado = "ACTIVO";

    @Column(name = "sincronizar_dian")
    private Long sincronizarDian;

    @Column(name = "id_documento")
    private Long idDocumento; // Tipo documento por defecto

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_documento", insertable = false, updatable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "id_tercero_defecto")
    private Long idTerceroDefecto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tercero_defecto", insertable = false, updatable = false)
    private com.data.datafacturador.entity.Tercero terceroDefecto;

    @Column(name = "id_lista_precio_defecto")
    private Long idListaPrecioDefecto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_lista_precio_defecto", insertable = false, updatable = false)
    private TipoLista tipoLista;

    @Column(name = "imprimir_qr", length = 55)
    private String imprimirQr;

    @Column(length = 55)
    private String buscador;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;



    @Column(length = 100)
    private String plan;

    @Column(name = "plan_limite_documentos")
    private Long planLimiteDocumentos;

    @Column(name = "plan_documentos_emitidos")
    private Long planDocumentosEmitidos;

    // Métodos de utilidad
    public boolean isActiva() {
        return "ACTIVO".equals(this.estado);
    }
}
