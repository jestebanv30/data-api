package com.data.datafacturador.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Entidad PermisoUsuario - Representa los permisos de un usuario sobre módulos
 * Mapea a la tabla data2.permiso_usuario
 */
@Entity
@Table(name = "permiso_usuario", schema = "data2")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @Column(name = "id_sucursal", nullable = false)
    private Long idSucursal; // Generalmente 1 (Principal) o específico

    @Column(name = "modulo", nullable = false, length = 100)
    private String modulo; // Ej: "ventas", "compras", "configuracion"

    @Column(name = "tipo_usuario", length = 50)
    private String tipoUsuario; // Rol: "administrador", "empleado", "superadmin"

    @Column(name = "estado", length = 20)
    private String estado; // "ACTIVO"

    @Column(name = "id_empleado")
    private Long idEmpleado;

    @Column(name = "fecha_creacion")
    private ZonedDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = ZonedDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "ACTIVO";
        }
    }
}
