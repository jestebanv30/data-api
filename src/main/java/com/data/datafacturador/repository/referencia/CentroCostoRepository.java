package com.data.datafacturador.repository.referencia;

import java.util.List;
import com.data.datafacturador.entity.referencia.CentroCosto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroCostoRepository extends JpaRepository<CentroCosto, Long> {
    List<CentroCosto> findByEmpresaId(Integer empresaId);

    @Query("SELECT t FROM CentroCosto t WHERE t.empresaId = :empresaId OR t.empresaId IS NULL ORDER BY t.id")
    List<CentroCosto> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
