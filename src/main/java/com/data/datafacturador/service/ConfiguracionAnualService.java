package com.data.datafacturador.service;

import com.data.datafacturador.entity.configuracion.Retencion;
import com.data.datafacturador.entity.configuracion.SalarioMinimo;
import com.data.datafacturador.entity.configuracion.Uvt;
import com.data.datafacturador.repository.configuracion.RetencionRepository;
import com.data.datafacturador.repository.configuracion.SalarioMinimoRepository;
import com.data.datafacturador.repository.configuracion.UvtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfiguracionAnualService {

    private final SalarioMinimoRepository salarioMinimoRepository;
    private final UvtRepository uvtRepository;
    private final RetencionRepository retencionRepository;
    private final com.data.datafacturador.repository.configuracion.ImpuestoRepository impuestoRepository;

    // --- Salario Minimo ---

    // --- Salario Minimo ---

    @Transactional(readOnly = true)
    public List<SalarioMinimo> listarSalariosMinimos(Integer empresaId) {
        return salarioMinimoRepository.findAllByEmpresaIdOrGlobal(empresaId);
    }

    @Transactional
    public SalarioMinimo guardarSalarioMinimo(SalarioMinimo salario, Integer empresaId, boolean esSuperAdmin) {
        // Validación de unicidad si es necesario, pero por ahora simple
        if (!esSuperAdmin) {
            if (empresaId == null) throw new SecurityException("Usuario normal no puede crear globales");
            salario.setEmpresaId(empresaId);
        } else {
             // Si es Super Admin, respetamos el empresaId que venga (null o valor)
        }
        return salarioMinimoRepository.save(salario);
    }
    
    @Transactional
    public SalarioMinimo actualizarSalarioMinimo(Long id, SalarioMinimo actualizado, Integer empresaId, boolean esSuperAdmin) {
        SalarioMinimo existente = salarioMinimoRepository.findById(id)
             .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado"));
             
        if (!esSuperAdmin) {
             validarPropiedadEmpresa(existente.getEmpresaId(), empresaId, "Salario Mínimo");
        }
        
        existente.setSalarioMinimo(actualizado.getSalarioMinimo());
        existente.setAuxilioTransporte(actualizado.getAuxilioTransporte());
        existente.setTopeSalarioAuxilio(actualizado.getTopeSalarioAuxilio());
        // anio no se suele cambiar al editar, pero se podria
        
        return salarioMinimoRepository.save(existente);
    }

    @Transactional
    public void eliminarSalarioMinimo(Long id, Integer empresaId, boolean esSuperAdmin) {
        SalarioMinimo existente = salarioMinimoRepository.findById(id)
             .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado"));
             
        if (!esSuperAdmin) {
             validarPropiedadEmpresa(existente.getEmpresaId(), empresaId, "Salario Mínimo");
        }
        salarioMinimoRepository.delete(existente);
    }
    
    // --- UVT ---

    @Transactional(readOnly = true)
    public Uvt obtenerUvt(Integer anio, Integer empresaId) {
        Optional<Uvt> especifico = uvtRepository.findByAnioAndEmpresaId(anio, empresaId);
        if (especifico.isPresent()) {
            return especifico.get();
        }
        return uvtRepository.findByAnioAndEmpresaIdIsNull(anio)
                .orElseThrow(() -> new IllegalArgumentException("No se ha configurado la UVT para el año " + anio));
    }

    @Transactional
    public Uvt guardarUvt(Uvt uvt, Integer empresaId) {
        if (empresaId != null) {
            uvt.setEmpresaId(empresaId);
        }
        return uvtRepository.save(uvt);
    }

    // --- Retenciones ---

    @Transactional(readOnly = true)
    public List<Retencion> listarRetenciones(Integer empresaId) {
        // Implementación Híbrida: Trae globales + locales de la empresa
        return retencionRepository.findAllByEmpresaIdOrGlobal(empresaId);
    }

    @Transactional
    public Retencion guardarRetencion(Retencion retencion, Integer empresaId) {
        if (empresaId != null) {
            retencion.setEmpresaId(empresaId);
        }
        return retencionRepository.save(retencion);
    }

    @Transactional
    public Retencion actualizarRetencion(Long id, Retencion retencionActualizada, Integer empresaId, boolean esSuperAdmin) {
        Retencion existente = retencionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Retención no encontrada"));

        if (!esSuperAdmin) {
            validarPropiedadEmpresa(existente.getEmpresaId(), empresaId, "Retención");
        }

        existente.setTipoRetencion(retencionActualizada.getTipoRetencion());
        existente.setConcepto(retencionActualizada.getConcepto());
        existente.setPorcentaje(retencionActualizada.getPorcentaje());
        existente.setBaseUvt(retencionActualizada.getBaseUvt());
        // Si es Super Admin, permitimos cambiar la empresa (o volverla global)
        if (esSuperAdmin && retencionActualizada.getEmpresaId() != null) {
             existente.setEmpresaId(retencionActualizada.getEmpresaId());
        }
        
        return retencionRepository.save(existente);
    }

    @Transactional
    public void eliminarRetencion(Long id, Integer empresaId, boolean esSuperAdmin) {
        Retencion existente = retencionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Retención no encontrada"));
        
        if (!esSuperAdmin) {
            validarPropiedadEmpresa(existente.getEmpresaId(), empresaId, "Retención");
        }
        
        retencionRepository.delete(existente);
    }
    
    // --- Impuestos ---

    @Transactional(readOnly = true)
    public List<com.data.datafacturador.entity.configuracion.Impuesto> listarImpuestos(Integer empresaId) {
        return impuestoRepository.findAllByEmpresaIdOrGlobal(empresaId);
    }

    @Transactional
    public com.data.datafacturador.entity.configuracion.Impuesto guardarImpuesto(com.data.datafacturador.entity.configuracion.Impuesto impuesto, Integer empresaId) {
        if (empresaId != null) {
            impuesto.setEmpresaId(empresaId);
        }
        return impuestoRepository.save(impuesto);
    }

    @Transactional
    public com.data.datafacturador.entity.configuracion.Impuesto actualizarImpuesto(Long id, com.data.datafacturador.entity.configuracion.Impuesto impuestoActualizado, Integer empresaId, boolean esSuperAdmin) {
        com.data.datafacturador.entity.configuracion.Impuesto existente = impuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impuesto no encontrado"));

        if (!esSuperAdmin) {
            validarPropiedadEmpresa(existente.getEmpresaId(), empresaId, "Impuesto");
        }

        existente.setPorcentaje(impuestoActualizado.getPorcentaje());
        if (esSuperAdmin && impuestoActualizado.getEmpresaId() != null) {
             existente.setEmpresaId(impuestoActualizado.getEmpresaId());
        }
        
        return impuestoRepository.save(existente);
    }

    @Transactional
    public void eliminarImpuesto(Long id, Integer empresaId, boolean esSuperAdmin) {
        com.data.datafacturador.entity.configuracion.Impuesto existente = impuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impuesto no encontrado"));

        if (!esSuperAdmin) {
            validarPropiedadEmpresa(existente.getEmpresaId(), empresaId, "Impuesto");
        }

        impuestoRepository.delete(existente);
    }

    private void validarPropiedadEmpresa(Integer entityEmpresaId, Integer requestEmpresaId, String entityName) {
        if (entityEmpresaId == null) {
            throw new IllegalStateException("No se puede modificar/eliminar un registro GLOBAL de " + entityName);
        }
        // requestEmpresaId puede ser null si es un usuario sin empresa asignada (raro pero posible), 
        // pero la validación aquí es estricta: coincidencia exacta.
        if (!entityEmpresaId.equals(requestEmpresaId)) {
            throw new SecurityException("No tiene permisos para modificar este registro de " + entityName);
        }
    }
    
    public Integer obtenerAnioActual() {
        return LocalDate.now().getYear();
    }
}
