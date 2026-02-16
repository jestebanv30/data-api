package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.TipoListaPrecios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoListaPreciosRepository extends JpaRepository<TipoListaPrecios, Long> {
}
