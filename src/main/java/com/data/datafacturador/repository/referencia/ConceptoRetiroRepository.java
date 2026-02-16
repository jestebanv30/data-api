package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.ConceptoRetiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConceptoRetiroRepository extends JpaRepository<ConceptoRetiro, Long> {
}
