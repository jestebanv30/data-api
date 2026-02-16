package com.data.datafacturador.repository.configuracion;

import com.data.datafacturador.entity.configuracion.Uvt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UvtRepository extends JpaRepository<Uvt, Long> {
    Optional<Uvt> findByAnioAndEmpresaId(Integer anio, Integer empresaId);
    Optional<Uvt> findByAnioAndEmpresaIdIsNull(Integer anio);
}
