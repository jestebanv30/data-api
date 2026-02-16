package com.data.datafacturador.repository.referencia;

import java.util.Optional;
import com.data.datafacturador.entity.referencia.TipoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoContratoRepository extends JpaRepository<TipoContrato, Long> {
    Optional<TipoContrato> findByCodigo(String codigo);
}
