package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.TipoLista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoListaRepository extends JpaRepository<TipoLista, Long> {
}
