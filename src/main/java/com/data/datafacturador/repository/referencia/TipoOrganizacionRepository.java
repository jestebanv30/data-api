package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.TipoOrganizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoOrganizacionRepository extends JpaRepository<TipoOrganizacion, Long> {
}
