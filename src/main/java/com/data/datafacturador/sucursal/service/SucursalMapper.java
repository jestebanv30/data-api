package com.data.datafacturador.sucursal.service;

import com.data.datafacturador.entity.referencia.*;
import com.data.datafacturador.sucursal.dto.ReferenciaDTO;
import com.data.datafacturador.sucursal.dto.SucursalDetailResponse;
import com.data.datafacturador.sucursal.dto.SucursalListResponse;
import com.data.datafacturador.sucursal.entity.Sucursal;
import com.data.datafacturador.repository.referencia.*;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Base64;

/**
 * Mapper para convertir entre Sucursal entity y DTOs
 */
@Component
@RequiredArgsConstructor
public class SucursalMapper {
    
    private final PaisRepository paisRepository;
    private final CiudadDepartamentoRepository ciudadDepartamentoRepository;

    /**
     * Convierte Sucursal a SucursalListResponse (vista resumida)
     */
    public SucursalListResponse toListResponse(Sucursal sucursal) {
        if (sucursal == null) {
            return null;
        }

        return SucursalListResponse.builder()
                .id(sucursal.getId())
                .empresaId(sucursal.getEmpresaId())
                .nombreComercial(sucursal.getNombreComercial())
                .nombreRazonSocial(sucursal.getNombreRazonSocial())
                .ccNit(sucursal.getCcNit())
                .ciudad(sucursal.getCiudad())
                .departamento(sucursal.getDepartamento())
                .estado(sucursal.getEstado())
                .activa(sucursal.isActiva())
                .build();
    }

    /**
     * Convierte Sucursal a SucursalDetailResponse (vista completa)
     */
    public SucursalDetailResponse toDetailResponse(Sucursal sucursal) {
        if (sucursal == null) {
            return null;
        }

        // Convertir logo a base64 si existe
        String logoBase64 = null;
        if (sucursal.getLogo() != null && sucursal.getLogo().length > 0) {
            logoBase64 = Base64.getEncoder().encodeToString(sucursal.getLogo());
        }

        return SucursalDetailResponse.builder()
                .id(sucursal.getId())
                .empresaId(sucursal.getEmpresaId())
                .nombreRazonSocial(sucursal.getNombreRazonSocial())
                .nombreComercial(sucursal.getNombreComercial())
                .ccNit(sucursal.getCcNit())
                .dv(sucursal.getDv())
                .tipoOrganizacion(toReferenciaDTO(sucursal.getTipoOrganizacion()))
                .tipoIdentificacion(toReferenciaDTO(sucursal.getTipoIdentificacion()))
                .responsabilidadFiscal(toReferenciaDTO(sucursal.getResponsabilidadFiscal()))
                .tipoRegimen(toReferenciaDTO(sucursal.getTipoRegimen()))
                .responsabilidadTributaria(toReferenciaDTO(sucursal.getResponsabilidadTributaria()))
                
                // Ubicación (mapeada a objetos)
                .pais(mapPais(sucursal.getPais()))
                .ciudad(mapCiudad(sucursal.getIdCiudad()))
                .direccion(sucursal.getDireccion())
                .departamento(sucursal.getDepartamento())
                
                .celular(sucursal.getCelular())
                .telefono(sucursal.getTelefono())
                .correo(sucursal.getCorreo())
                .slogan(sucursal.getSlogan())
                .sloganOrden(sucursal.getSloganOrden())
                .copiasVentas(sucursal.getCopiasVentas())
                .copiasCompras(sucursal.getCopiasCompras())
                .copiasCobros(sucursal.getCopiasCobros())
                .copiasPagos(sucursal.getCopiasPagos())
                .logoBase64(logoBase64)
                .estado(sucursal.getEstado())
                .activa(sucursal.isActiva())
                .sincronizarDian(sucursal.getSincronizarDian())
                .sincronizarDian(sucursal.getSincronizarDian())
                .tipoDocumento(toReferenciaDTO(sucursal.getTipoDocumento()))
                .terceroDefecto(toTerceroDTO(sucursal.getTerceroDefecto()))
                .tipoListaPrecioDefecto(toReferenciaDTO(sucursal.getTipoLista()))
                .imprimirQr(sucursal.getImprimirQr())
                .buscador(sucursal.getBuscador())
                .plan(sucursal.getPlan())
                .planLimiteDocumentos(sucursal.getPlanLimiteDocumentos())
                .planDocumentosEmitidos(sucursal.getPlanDocumentosEmitidos())
                .idUsuario(sucursal.getIdUsuario())
                .fechaActualizacion(sucursal.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte una entidad de referencia a ReferenciaDTO
     */
    private ReferenciaDTO toReferenciaDTO(TipoOrganizacion entity) {
        if (entity == null)
            return null;
        return ReferenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(entity.getCodigo())
                .build();
    }

    private ReferenciaDTO toReferenciaDTO(TipoIdentificacion entity) {
        if (entity == null)
            return null;
        return ReferenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(entity.getCodigo())
                .build();
    }

    private ReferenciaDTO toReferenciaDTO(ResponsabilidadFiscal entity) {
        if (entity == null)
            return null;
        return ReferenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(entity.getCodigo())
                .build();
    }

    private ReferenciaDTO toReferenciaDTO(TipoRegimen entity) {
        if (entity == null)
            return null;
        return ReferenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(entity.getCodigo())
                .build();
    }

    private ReferenciaDTO toReferenciaDTO(TipoDocumento entity) {
        if (entity == null) return null;
        return ReferenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(entity.getCodigo())
                .build();
    }

    private ReferenciaDTO toReferenciaDTO(TipoLista entity) {
        if (entity == null) return null;
        return ReferenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(null) // TipoLista no tiene código
                .build();
    }

    private ReferenciaDTO toReferenciaDTO(ResponsabilidadTributaria entity) {
        if (entity == null)
            return null;
        return ReferenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(entity.getCodigo())
                .build();
    }
    
    private com.data.datafacturador.sucursal.dto.TerceroSimpleDTO toTerceroDTO(com.data.datafacturador.entity.Tercero entity) {
        if (entity == null) return null;
        return com.data.datafacturador.sucursal.dto.TerceroSimpleDTO.builder()
                .id(entity.getId())
                .nombreRazonSocial(entity.getNombreRazonSocial())
                .dv(entity.getDv())
                .build();
    }
    
    private ReferenciaDTO mapPais(String nombrePais) {
        if (nombrePais == null) return null;
        return paisRepository.findByNombre(nombrePais)
                .map(p -> new ReferenciaDTO(p.getId(), p.getNombre(), p.getCodigo()))
                .orElse(new ReferenciaDTO(null, nombrePais, null)); // Fallback si no encuentra
    }

    private ReferenciaDTO mapCiudad(String idCiudadStr) {
        if (idCiudadStr == null) return null;
        try {
            Long id = Long.parseLong(idCiudadStr);
            return ciudadDepartamentoRepository.findById(id)
                    .map(c -> new ReferenciaDTO(c.getId(), c.getNombre(), c.getIdDepartamento()))
                    .orElse(null);
        } catch (NumberFormatException e) {
            return null; // Si el ID no es numérico (legacy data corrompida)
        }
    }
}
