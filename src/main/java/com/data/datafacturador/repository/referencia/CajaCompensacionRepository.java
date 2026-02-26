package com.data.datafacturador.repository.referencia;

import java.util.List;
import com.data.datafacturador.entity.referencia.CajaCompensacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CajaCompensacionRepository extends JpaRepository<CajaCompensacion, Long> {
    @Query("SELECT t FROM CajaCompensacion t WHERE t.empresaId = :empresaId OR t.empresaId IS NULL ORDER BY t.id")
    List<CajaCompensacion> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
