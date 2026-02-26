package com.data.datafacturador.repository.referencia;

import java.util.List;
import com.data.datafacturador.entity.referencia.FondoSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FondoSaludRepository extends JpaRepository<FondoSalud, Long> {
    @Query("SELECT t FROM FondoSalud t WHERE t.empresaId = :empresaId OR t.empresaId IS NULL ORDER BY t.id")
    List<FondoSalud> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
