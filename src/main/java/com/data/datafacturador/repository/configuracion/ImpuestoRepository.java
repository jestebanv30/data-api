package com.data.datafacturador.repository.configuracion;

import com.data.datafacturador.entity.configuracion.Impuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImpuestoRepository extends JpaRepository<Impuesto, Long> {

    @Query("SELECT i FROM Impuesto i WHERE i.empresaId = :empresaId OR i.empresaId IS NULL ORDER BY i.id ASC")
    List<Impuesto> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
