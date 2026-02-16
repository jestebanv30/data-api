package com.data.datafacturador.repository.nomina;

import com.data.datafacturador.entity.nomina.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BancoRepository extends JpaRepository<Banco, Long> {
    List<Banco> findByEmpresaId(Integer empresaId);
}
