package com.data.datafacturador.repository.referencia;

import com.data.datafacturador.entity.referencia.CiudadDepartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CiudadDepartamentoRepository extends JpaRepository<CiudadDepartamento, Long> {
    List<CiudadDepartamento> findByDepartamento(String departamento);
}
