package com.data.datafacturador.repository;

import com.data.datafacturador.entity.Tercero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TerceroRepository extends JpaRepository<Tercero, Long> {
    Optional<Tercero> findByIdAndEmpresaId(Long id, Integer empresaId);
}
