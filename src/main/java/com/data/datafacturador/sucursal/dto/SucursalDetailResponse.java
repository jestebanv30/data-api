package com.data.datafacturador.sucursal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * DTO completo para detalle de sucursal
 * Incluye TODOS los campos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalDetailResponse {

    private Long id;
    private Integer empresaId;
    private String nombreRazonSocial;
    private String nombreComercial;
    private String ccNit;
    private String dv;

    // Datos de referencia (con nombres legibles)
    private ReferenciaDTO tipoOrganizacion;
    private ReferenciaDTO tipoIdentificacion;
    private ReferenciaDTO responsabilidadFiscal;
    private ReferenciaDTO tipoRegimen;
    private ReferenciaDTO responsabilidadTributaria;

    // Ubicación
    // Ubicación
    private ReferenciaDTO pais; // Antes String
    private ReferenciaDTO ciudad; // Antes String + idCiudad
    private String direccion;
    // Departamento se puede incluir dentro de ciudad.nombre o separado. Lo dejo separado si es solo texto, o lo quito si va en ciudad.
    // El usuario mostró DB ciudad_departamento.
    private String departamento; // Mantengo por compatibilidad visual simple


    // Contacto
    private String celular;
    private String telefono;
    private String correo;

    // Configuración
    private String slogan;
    private String sloganOrden;
    private Long copiasVentas;
    private Long copiasCompras;
    private Long copiasCobros;
    private Long copiasPagos;
    private String logoBase64; // Logo en base64

    // Estado y configuración
    private String estado;
    private Boolean activa;
    private Long sincronizarDian;

    // Configuración de documentos
    private ReferenciaDTO tipoDocumento; // Antes idDocumento
    private TerceroSimpleDTO terceroDefecto; // Antes idTerceroDefecto
    private ReferenciaDTO tipoListaPrecioDefecto; // Antes idListaPrecioDefecto
    private String imprimirQr;
    private String buscador;

    // Plan
    private String plan;
    private Long planLimiteDocumentos;
    private Long planDocumentosEmitidos;

    // Auditoría
    private Long idUsuario;
    private ZonedDateTime fechaActualizacion;
}
