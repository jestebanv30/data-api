package com.data.datafacturador.sucursal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar una sucursal
 * Contiene los campos editables y los IDs de las referencias
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalRequest {

    // Datos Básicos
    private String nombreComercial;
    private String nombreRazonSocial;
    private String ccNit;
    private String dv;
    
    // Ubicación
    private String pais;
    private String ciudad; // ID ciudad
    private String departamento;
    private String direccion;
    
    // Contacto
    private String celular;
    private String telefono;
    private String correo;
    
    // Configuración Visual
    private String slogan;
    private String sloganOrden;
    private String logoBase64; // Base64 del logo si se actualiza
    
    // IDs de referencias (dropdowns)
    private Long idTipoOrganizacion;
    private Long idTipoIdentificacion;
    private Long idResponsabilidadFiscal;
    private Long idTipoRegimen;
    private Long idTipoResponsabilidadTributaria;
    
    // Configuración Operativa
    private Long copiasVentas;
    private Long copiasCompras;
    private Long copiasCobros;
    private Long copiasPagos;
    private Long sincronizarDian;
    private Long idDocumento; // ID nuevo
    private Long idTerceroDefecto;
    private Long idListaPrecioDefecto; // ID nuevo
    private String imprimirQr;
    private String buscador;
}
