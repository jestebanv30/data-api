package com.data.datafacturador.repository;

import com.data.datafacturador.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Empresa
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    /**
     * Busca una empresa por su NIT
     */
    Optional<Empresa> findByNit(String nit);

    /**
     * Busca una empresa por su nombre
     */
    Optional<Empresa> findByNombre(String nombre);

    /**
     * Verifica si existe una empresa con el NIT dado
     */
    boolean existsByNit(String nit);
}
