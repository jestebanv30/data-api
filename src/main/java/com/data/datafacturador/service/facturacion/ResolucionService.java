package com.data.datafacturador.service.facturacion;

import com.data.datafacturador.entity.facturacion.ResolucionTipoDocumento;
import com.data.datafacturador.sucursal.entity.Sucursal;
import com.data.datafacturador.sucursal.repository.SucursalRepository;
import com.data.datafacturador.repository.facturacion.ResolucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResolucionService {

    private final ResolucionRepository resolucionRepository;
    private final SucursalRepository sucursalRepository;

    // -------------------------------------------------------
    // LECTURA
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ResolucionTipoDocumento> listarPorSucursal(Long idSucursal, Integer empresaIdUsuario) {
        validarPropiedadSucursal(idSucursal, empresaIdUsuario);
        return resolucionRepository.findByIdSucursal(idSucursal);
    }

    @Transactional(readOnly = true)
    public ResolucionTipoDocumento obtenerPorId(Long id, Integer empresaIdUsuario) {
        return resolucionRepository.findByIdAndEmpresaId(id, empresaIdUsuario)
                .orElseThrow(() -> new SecurityException("Resolución no encontrada o no pertenece a su empresa."));
    }

    // -------------------------------------------------------
    // CREACIÓN
    // -------------------------------------------------------

    @Transactional
    public ResolucionTipoDocumento crearResolucion(ResolucionTipoDocumento resolucion, Integer empresaIdUsuario) {
        if (resolucion.getIdSucursal() == null) {
            throw new IllegalArgumentException("La resolución debe estar asignada a una sucursal.");
        }
        validarPropiedadSucursal(resolucion.getIdSucursal(), empresaIdUsuario);

        resolucion.setEmpresaId(empresaIdUsuario);
        // Siempre inicia en 0
        resolucion.setCorrelativo(BigDecimal.ZERO);
        resolucion.setCorrelativoNc(BigDecimal.ZERO);
        resolucion.setCorrelativoNd(BigDecimal.ZERO);
        // Limpiar campos técnicos — solo SuperAdmin los configura después
        resolucion.setIdSoftware(null);
        resolucion.setPin(null);
        resolucion.setTestSetId(null);
        resolucion.setClaveTecnica(null);
        resolucion.setSetPruebas("NO"); // Por defecto, producción

        return resolucionRepository.save(resolucion);
    }

    // -------------------------------------------------------
    // ACTUALIZACIÓN (Admin Empresa) — campos de resolución
    // -------------------------------------------------------

    @Transactional
    public ResolucionTipoDocumento actualizarResolucion(Long id, ResolucionTipoDocumento datosNuevos, Integer empresaIdUsuario) {
        ResolucionTipoDocumento existente = resolucionRepository.findByIdAndEmpresaId(id, empresaIdUsuario)
                .orElseThrow(() -> new SecurityException("Resolución no encontrada o no pertenece a su empresa."));

        // Si cambia de sucursal, validar propiedad de la nueva
        if (datosNuevos.getIdSucursal() != null && !existente.getIdSucursal().equals(datosNuevos.getIdSucursal())) {
            validarPropiedadSucursal(datosNuevos.getIdSucursal(), empresaIdUsuario);
            existente.setIdSucursal(datosNuevos.getIdSucursal());
        }

        // Detectar si los campos clave de la resolución cambian → resetear correlativo
        boolean resolucionCambia =
                !equals(existente.getNumeroResolucion(), datosNuevos.getNumeroResolucion()) ||
                !equals(existente.getPrefijo(), datosNuevos.getPrefijo())                  ||
                !equals(existente.getRangoDesde(), datosNuevos.getRangoDesde())            ||
                !equals(existente.getRangoHasta(), datosNuevos.getRangoHasta());

        if (resolucionCambia) {
            existente.setCorrelativo(BigDecimal.ZERO);
            existente.setCorrelativoNc(BigDecimal.ZERO);
            existente.setCorrelativoNd(BigDecimal.ZERO);
        }

        // Actualizar campos permitidos para el Admin (NO los técnicos de software DIAN)
        existente.setPrefijo(datosNuevos.getPrefijo());
        existente.setNumeroResolucion(datosNuevos.getNumeroResolucion());
        existente.setFechaDesde(datosNuevos.getFechaDesde());
        existente.setFechaHasta(datosNuevos.getFechaHasta());
        existente.setRangoDesde(datosNuevos.getRangoDesde());
        existente.setRangoHasta(datosNuevos.getRangoHasta());
        existente.setRangoAlerta(datosNuevos.getRangoAlerta());
        existente.setIdTipoDocumento(datosNuevos.getIdTipoDocumento());
        existente.setPrefijoNc(datosNuevos.getPrefijoNc());
        existente.setPrefijoNd(datosNuevos.getPrefijoNd());

        // NOTA: idSoftware, pin, testSetId, claveTecnica son IGNORADOS aquí.
        // Solo el SuperAdmin puede configurarlos via actualizarCredenciales()

        return resolucionRepository.save(existente);
    }

    // -------------------------------------------------------
    // ACTUALIZACIÓN DE CREDENCIALES TÉCNICAS — Solo SuperAdmin
    // -------------------------------------------------------

    @Transactional
    public ResolucionTipoDocumento actualizarCredenciales(Long id, String idSoftware, String pin,
                                                           String testSetId, String claveTecnica,
                                                           String setPruebas) {
        ResolucionTipoDocumento existente = resolucionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resolución no encontrada: " + id));

        if (idSoftware  != null) existente.setIdSoftware(idSoftware);
        if (pin         != null) existente.setPin(pin);
        if (testSetId   != null) existente.setTestSetId(testSetId);
        if (claveTecnica != null) existente.setClaveTecnica(claveTecnica);
        if (setPruebas  != null) existente.setSetPruebas(setPruebas);

        return resolucionRepository.save(existente);
    }

    // -------------------------------------------------------
    // ELIMINACIÓN
    // -------------------------------------------------------

    @Transactional
    public void eliminarResolucion(Long id, Integer empresaIdUsuario) {
        ResolucionTipoDocumento existente = resolucionRepository.findByIdAndEmpresaId(id, empresaIdUsuario)
                .orElseThrow(() -> new SecurityException("Resolución no encontrada o no pertenece a su empresa."));
        resolucionRepository.delete(existente);
    }

    // -------------------------------------------------------
    // SuperAdmin: listar por cualquier sucursal sin restricción
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ResolucionTipoDocumento> listarPorSucursalAdmin(Long idSucursal) {
        return resolucionRepository.findByIdSucursal(idSucursal);
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private void validarPropiedadSucursal(Long idSucursal, Integer empresaIdUsuario) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));
        if (sucursal.getEmpresaId() == null || !sucursal.getEmpresaId().equals(empresaIdUsuario)) {
            throw new SecurityException("La sucursal no pertenece a su empresa.");
        }
    }

    private boolean equals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).compareTo((BigDecimal) b) == 0;
        }
        return a.equals(b);
    }
}
