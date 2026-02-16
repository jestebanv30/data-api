package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.ResponsabilidadTributaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsabilidadTributariaRepository extends JpaRepository<ResponsabilidadTributaria, Long> {
}
