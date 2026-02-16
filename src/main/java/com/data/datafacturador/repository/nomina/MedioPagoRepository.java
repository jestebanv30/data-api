package com.data.datafacturador.repository.nomina;

import com.data.datafacturador.entity.nomina.MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedioPagoRepository extends JpaRepository<MedioPago, Long> {
    List<MedioPago> findByEmpresaId(Integer empresaId);
}
