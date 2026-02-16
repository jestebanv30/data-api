package com.data.datafacturador.service;

import com.data.datafacturador.dto.EmpresaResponse;
import com.data.datafacturador.entity.Empresa;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Empresa entity y EmpresaResponse DTO
 */
@Component
public class EmpresaMapper {

    /**
     * Convierte una entidad Empresa a EmpresaResponse DTO
     */
    public EmpresaResponse toResponse(Empresa empresa) {
        if (empresa == null) {
            return null;
        }

        return EmpresaResponse.builder()
                .id(empresa.getId())
                .nombre(empresa.getNombre())
                .razonSocial(empresa.getRazonSocial())
                .nit(empresa.getNit())
                .direccion(empresa.getDireccion())
                .ciudad(empresa.getCiudad())
                .departamento(empresa.getDepartamento())
                .pais(empresa.getPais())
                .telefono(empresa.getTelefono())
                .celular(empresa.getCelular())
                .email(empresa.getEmail())
                .sitioWeb(empresa.getSitioWeb())
                .logoUrl(empresa.getLogoUrl())
                .datosPrecargados(empresa.getDatosPrecargados())
                .configuracion(empresa.getConfiguracion())
                .fechaCreacion(empresa.getFechaCreacion())
                .fechaActualizacion(empresa.getFechaActualizacion())
                .creadoPor(empresa.getCreadoPor())
                .actualizadoPor(empresa.getActualizadoPor())
                .estado(empresa.getEstado())
                .activo(empresa.getActivo())
                .build();
    }
}
