package com.data.datafacturador.repository.referencia;

import java.util.Optional;
import com.data.datafacturador.entity.referencia.TipoTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoTrabajadorRepository extends JpaRepository<TipoTrabajador, Long> {
    Optional<TipoTrabajador> findByCodigo(String codigo);
}
