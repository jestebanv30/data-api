package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.ResponsabilidadFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsabilidadFiscalRepository extends JpaRepository<ResponsabilidadFiscal, Long> {
}
