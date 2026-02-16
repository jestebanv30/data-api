package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.TipoPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPeriodoRepository extends JpaRepository<TipoPeriodo, Long> {
}
