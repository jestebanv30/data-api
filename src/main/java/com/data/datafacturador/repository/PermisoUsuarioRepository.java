package com.data.datafacturador.repository;

import com.data.datafacturador.entity.PermisoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para PermisoUsuario
 */
@Repository
public interface PermisoUsuarioRepository extends JpaRepository<PermisoUsuario, Long> {

    /**
     * Busca los permisos activos de un usuario en una empresa específica
     */
    List<PermisoUsuario> findByIdUsuarioAndEmpresaIdAndEstado(Long idUsuario, Integer empresaId, String estado);

    /**
     * Busca todos los permisos de un usuario
     */
    List<PermisoUsuario> findByIdUsuario(Long idUsuario);

    /**
     * Elimina todos los permisos de un usuario en una empresa
     */
    void deleteByIdUsuarioAndEmpresaId(Long idUsuario, Integer empresaId);

    /**
     * Elimina todos los permisos de un usuario (global)
     */
    void deleteByIdUsuario(Long idUsuario);
}
