package com.data.datafacturador.repository.referencia;

import java.util.List;
import java.util.Optional;
import com.data.datafacturador.entity.referencia.TipoTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoTrabajadorRepository extends JpaRepository<TipoTrabajador, Long> {
    Optional<TipoTrabajador> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("SELECT t FROM TipoTrabajador t WHERE t.empresaId = :empresaId OR t.empresaId IS NULL ORDER BY t.id")
    List<TipoTrabajador> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
