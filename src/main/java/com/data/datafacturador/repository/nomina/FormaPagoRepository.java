package com.data.datafacturador.repository.nomina;

import java.util.List;
import com.data.datafacturador.entity.nomina.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    List<FormaPago> findByEmpresaId(Integer empresaId);

    @Query("SELECT f FROM FormaPago f WHERE f.empresaId = :empresaId OR f.empresaId IS NULL ORDER BY f.id")
    List<FormaPago> findAllByEmpresaIdOrGlobal(@Param("empresaId") Integer empresaId);
}
