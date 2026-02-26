package com.data.datafacturador.repository.referencia;

import java.util.List;
import com.data.datafacturador.entity.referencia.FondoCesantias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FondoCesantiasRepository extends JpaRepository<FondoCesantias, Long> {
    @Query("SELECT t FROM FondoCesantias t WHERE t.empresaId = :empresaId OR t.empresaId IS NULL ORDER BY t.id")
    List<FondoCesantias> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
