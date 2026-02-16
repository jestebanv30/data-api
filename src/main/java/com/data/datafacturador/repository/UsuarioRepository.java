package com.data.datafacturador.repository;

import com.data.datafacturador.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * IMPORTANTE: Todos los métodos deben filtrar por empresa_id
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por nombre de usuario (para login)
     */
    Optional<Usuario> findByUsuario(String usuario);

    /**
     * Busca un usuario por username (alias para compatibilidad con Spring Security)
     */
    default Optional<Usuario> findByUsername(String username) {
        return findByUsuario(username);
    }

    /**
     * Busca un usuario por nombre de usuario y empresa_id
     */
    Optional<Usuario> findByUsuarioAndEmpresaId(String usuario, Integer empresaId);

    /**
     * Lista todos los usuarios de una empresa (paginado)
     */
    org.springframework.data.domain.Page<Usuario> findByEmpresaId(Integer empresaId, org.springframework.data.domain.Pageable pageable);

    /**
     * Lista todos los usuarios de una empresa
     */
    List<Usuario> findByEmpresaId(Integer empresaId);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     */
    boolean existsByUsuario(String usuario);

    /**
     * Verifica si existe un usuario con el nombre de usuario en una empresa
     * específica
     */
    boolean existsByUsuarioAndEmpresaId(String usuario, Integer empresaId);

    /**
     * Busca usuarios por id_empleado
     */
    Optional<Usuario> findByIdEmpleado(Long idEmpleado);

    /**
     * Busca un usuario por ID y empresa_id
     */
    Optional<Usuario> findByIdAndEmpresaId(Long id, Integer empresaId);
}
