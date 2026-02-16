package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.TipoRegimen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRegimenRepository extends JpaRepository<TipoRegimen, Long> {
}
