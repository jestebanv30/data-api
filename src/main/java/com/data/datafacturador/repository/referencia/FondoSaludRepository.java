package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.FondoSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FondoSaludRepository extends JpaRepository<FondoSalud, Long> {
}
