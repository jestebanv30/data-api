package com.data.datafacturador.repository.nomina;

import com.data.datafacturador.entity.nomina.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    List<FormaPago> findByEmpresaId(Integer empresaId);
}
