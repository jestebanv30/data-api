package com.data.datafacturador.repository.configuracion;

import com.data.datafacturador.entity.configuracion.Retencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RetencionRepository extends JpaRepository<Retencion, Long> {

    @Query("SELECT r FROM Retencion r WHERE r.empresaId = :empresaId OR r.empresaId IS NULL ORDER BY r.tipoRetencion ASC, r.baseUvt ASC")
    List<Retencion> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);

    // Métodos actualizados si se requieren filtros específicos
    List<Retencion> findByTipoRetencionAndEmpresaId(String tipoRetencion, Integer empresaId);
}
