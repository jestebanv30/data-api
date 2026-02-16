package com.data.datafacturador.service.referencia;

import com.data.datafacturador.repository.referencia.*;
import com.data.datafacturador.sucursal.dto.ReferenciaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para listar datos de tablas de referencia
 */
@Service
@RequiredArgsConstructor
public class ReferenciaService {

    private final TipoRegimenRepository tipoRegimenRepository;
    private final ResponsabilidadTributariaRepository responsabilidadTributariaRepository;
    private final TipoOrganizacionRepository tipoOrganizacionRepository;
    private final TipoIdentificacionRepository tipoIdentificacionRepository;
    private final ResponsabilidadFiscalRepository responsabilidadFiscalRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoListaRepository tipoListaRepository;
    private final PaisRepository paisRepository;
    private final CiudadDepartamentoRepository ciudadDepartamentoRepository;

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarTiposRegimen() {
        return tipoRegimenRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), e.getCodigo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarResponsabilidadesTributarias() {
        return responsabilidadTributariaRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), e.getCodigo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarTiposOrganizacion() {
        return tipoOrganizacionRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), e.getCodigo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarTiposIdentificacion() {
        return tipoIdentificacionRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), e.getCodigo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarResponsabilidadesFiscales() {
        return responsabilidadFiscalRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), e.getCodigo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarTiposDocumento() {
        return tipoDocumentoRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), e.getCodigo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarTiposLista() {
        return tipoListaRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), null)) // TipoLista no tiene código
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarPaises() {
        return paisRepository.findAll().stream()
                .map(e -> new ReferenciaDTO(e.getId(), e.getNombre(), e.getCodigo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferenciaDTO> listarCiudades() {
        return ciudadDepartamentoRepository.findAll().stream()
                .map(e -> ReferenciaDTO.builder()
                        .id(e.getId())
                        .nombre(e.getNombre() + " (" + e.getDepartamento() + ")") // Combinar nombre y departamento
                        .codigo(e.getIdDepartamento())
                        .build())
                .collect(Collectors.toList());
    }
}
