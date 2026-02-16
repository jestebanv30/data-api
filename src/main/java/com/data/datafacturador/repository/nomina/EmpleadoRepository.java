package com.data.datafacturador.repository.nomina;

import com.data.datafacturador.entity.nomina.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Page<Empleado> findByEmpresaId(Integer empresaId, Pageable pageable);
    List<Empleado> findByEmpresaId(Integer empresaId);
    Optional<Empleado> findByIdAndEmpresaId(Long id, Integer empresaId);
    boolean existsByCorreoAndEmpresaId(String correo, Integer empresaId);
    boolean existsByCcAndEmpresaId(String cc, Integer empresaId);
    
    @Query("SELECT e FROM Empleado e WHERE e.empresaId = :empresaId AND e.usuario IS NULL AND e.estado = 'ACTIVO' ORDER BY e.apellidos, e.nombres")
    List<Empleado> findEmpleadosSinUsuario(@Param("empresaId") Integer empresaId);
}
