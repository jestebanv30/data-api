package com.data.datafacturador.repository.configuracion;

import com.data.datafacturador.entity.configuracion.ConceptoImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConceptoImpuestoRepository extends JpaRepository<ConceptoImpuesto, Long> {

    @Query("SELECT c FROM ConceptoImpuesto c WHERE c.empresaId = :empresaId OR c.empresaId IS NULL ORDER BY c.conceptoImpuesto ASC")
    List<ConceptoImpuesto> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
