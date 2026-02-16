package com.data.datafacturador.sucursal.repository;

import com.data.datafacturador.sucursal.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Sucursal
 */
@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    /**
     * Busca todas las sucursales de una empresa
     */
    List<Sucursal> findByEmpresaId(Integer empresaId);

    /**
     * Busca todas las sucursales activas de una empresa
     */
    List<Sucursal> findByEmpresaIdAndEstado(Integer empresaId, String estado);

    /**
     * Busca una sucursal por ID y empresa_id (para seguridad multi-tenancy)
     */
    Optional<Sucursal> findByIdAndEmpresaId(Long id, Integer empresaId);

    /**
     * Cuenta sucursales de una empresa
     */
    long countByEmpresaId(Integer empresaId);

    /**
     * Cuenta sucursales activas de una empresa
     */
    long countByEmpresaIdAndEstado(Integer empresaId, String estado);
}
