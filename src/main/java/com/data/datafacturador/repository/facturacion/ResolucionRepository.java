package com.data.datafacturador.repository.facturacion;

import com.data.datafacturador.entity.facturacion.ResolucionTipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResolucionRepository extends JpaRepository<ResolucionTipoDocumento, Long> {
    
    // Listar resoluciones de una sucursal específica
    List<ResolucionTipoDocumento> findByIdSucursal(Long idSucursal);
    
    // Listar por empresa (si se quisiera ver todas)
    List<ResolucionTipoDocumento> findByEmpresaId(Integer empresaId);
    
    // Buscar por ID y Empresa (para seguridad en edición)
    java.util.Optional<ResolucionTipoDocumento> findByIdAndEmpresaId(Long id, Integer empresaId);
}
