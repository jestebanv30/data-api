package com.data.datafacturador.repository.referencia;

import java.util.List;
import com.data.datafacturador.entity.referencia.SubtipoCotizante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtipoCotizanteRepository extends JpaRepository<SubtipoCotizante, Long> {
    @Query("SELECT t FROM SubtipoCotizante t WHERE t.empresaId = :empresaId OR t.empresaId IS NULL ORDER BY t.id")
    List<SubtipoCotizante> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
