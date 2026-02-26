package com.data.datafacturador.repository.referencia;

import java.util.List;
import java.util.Optional;
import com.data.datafacturador.entity.referencia.TipoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoContratoRepository extends JpaRepository<TipoContrato, Long> {
    Optional<TipoContrato> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("SELECT t FROM TipoContrato t WHERE t.empresaId = :empresaId OR t.empresaId IS NULL ORDER BY t.id")
    List<TipoContrato> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
