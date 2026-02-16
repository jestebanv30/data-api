package com.data.datafacturador.service.facturacion;

import com.data.datafacturador.entity.facturacion.ResolucionTipoDocumento;
import com.data.datafacturador.sucursal.entity.Sucursal;
import com.data.datafacturador.sucursal.repository.SucursalRepository;
import com.data.datafacturador.repository.facturacion.ResolucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResolucionService {

    private final ResolucionRepository resolucionRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<ResolucionTipoDocumento> listarPorSucursal(Long idSucursal, Integer empresaIdUsuario) {
        validarPropiedadSucursal(idSucursal, empresaIdUsuario);
        return resolucionRepository.findByIdSucursal(idSucursal);
    }

    @Transactional
    public ResolucionTipoDocumento crearResolucion(ResolucionTipoDocumento resolucion, Integer empresaIdUsuario) {
        // Validar que la sucursal asignada pertenezca a la empresa del usuario
        if (resolucion.getIdSucursal() == null) {
            throw new IllegalArgumentException("La resolución debe estar asignada a una sucursal.");
        }
        validarPropiedadSucursal(resolucion.getIdSucursal(), empresaIdUsuario);

        // Asignar empresaId por seguridad query
        resolucion.setEmpresaId(empresaIdUsuario);
        
        // Validaciones de negocio adicionales (fechas, rangos) podrían ir aquí

        return resolucionRepository.save(resolucion);
    }

    @Transactional
    public ResolucionTipoDocumento actualizarResolucion(Long id, ResolucionTipoDocumento datosNuevos, Integer empresaIdUsuario) {
        ResolucionTipoDocumento existente = resolucionRepository.findByIdAndEmpresaId(id, empresaIdUsuario)
                .orElseThrow(() -> new SecurityException("Resolución no encontrada o no pertenece a su empresa."));

        // Si intenta cambiar de sucursal, validar propiedad de la nueva
        if (!existente.getIdSucursal().equals(datosNuevos.getIdSucursal())) {
            validarPropiedadSucursal(datosNuevos.getIdSucursal(), empresaIdUsuario);
            existente.setIdSucursal(datosNuevos.getIdSucursal());
        }

        // Actualizar campos
        existente.setPrefijo(datosNuevos.getPrefijo());
        existente.setNumeroResolucion(datosNuevos.getNumeroResolucion());
        existente.setFechaDesde(datosNuevos.getFechaDesde());
        existente.setFechaHasta(datosNuevos.getFechaHasta());
        existente.setRangoDesde(datosNuevos.getRangoDesde());
        existente.setRangoHasta(datosNuevos.getRangoHasta());
        existente.setClaveTecnica(datosNuevos.getClaveTecnica());
        existente.setTestSetId(datosNuevos.getTestSetId());
        existente.setPin(datosNuevos.getPin());
        existente.setIdSoftware(datosNuevos.getIdSoftware());
        // Tipos documento, etc.
        existente.setIdTipoDocumento(datosNuevos.getIdTipoDocumento());
        
        // Actualizar prefijos NC/ND si aplican
        existente.setPrefijoNc(datosNuevos.getPrefijoNc());
        existente.setPrefijoNd(datosNuevos.getPrefijoNd());

        return resolucionRepository.save(existente);
    }

    @Transactional
    public void eliminarResolucion(Long id, Integer empresaIdUsuario) {
        ResolucionTipoDocumento existente = resolucionRepository.findByIdAndEmpresaId(id, empresaIdUsuario)
                .orElseThrow(() -> new SecurityException("Resolución no encontrada o no pertenece a su empresa."));
        
        resolucionRepository.delete(existente);
    }

    private void validarPropiedadSucursal(Long idSucursal, Integer empresaIdUsuario) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));
        
        // Validar que la sucursal es de la empresa del usuario
        // Sucursal tiene empresaId directo (Integer)
        if (sucursal.getEmpresaId() == null || !sucursal.getEmpresaId().equals(empresaIdUsuario)) {
            throw new SecurityException("La sucursal no pertenece a su empresa.");
        }
    }
}
