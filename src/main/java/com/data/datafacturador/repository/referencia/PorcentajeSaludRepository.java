package com.data.datafacturador.repository.referencia;

import java.util.List;
import com.data.datafacturador.entity.referencia.PorcentajeSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PorcentajeSaludRepository extends JpaRepository<PorcentajeSalud, Long> {
    @Query("SELECT p FROM PorcentajeSalud p WHERE p.empresaId = :empresaId OR p.empresaId IS NULL ORDER BY p.porcentaje")
    List<PorcentajeSalud> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
