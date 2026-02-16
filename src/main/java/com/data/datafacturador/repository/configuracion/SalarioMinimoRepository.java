package com.data.datafacturador.repository.configuracion;

import com.data.datafacturador.entity.configuracion.SalarioMinimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalarioMinimoRepository extends JpaRepository<SalarioMinimo, Long> {
    
    // Buscar configuración específica de la empresa para un año
    Optional<SalarioMinimo> findByAnioAndEmpresaId(Integer anio, Integer empresaId);

    // Buscar configuración global del sistema para un año
    Optional<SalarioMinimo> findByAnioAndEmpresaIdIsNull(Integer anio);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM SalarioMinimo s WHERE s.empresaId = :empresaId OR s.empresaId IS NULL ORDER BY s.anio DESC")
    java.util.List<SalarioMinimo> findAllByEmpresaIdOrGlobal(@org.springframework.data.repository.query.Param("empresaId") Integer empresaId);
}
