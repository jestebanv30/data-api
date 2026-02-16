package com.data.datafacturador.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Usuario - Representa un usuario del sistema
 * Cada usuario pertenece a una empresa específica
 * 
 * IMPORTANTE: Mapea a la tabla data2.usuario con su estructura real:
 * - id (bigint)
 * - usuario (varchar) - nombre de usuario/email
 * - clave (varchar) - password encriptado
 * - id_empleado (bigint) - referencia a empleado (puede ser null)
 * - empresa_id (integer)
 */
@Entity
@Table(name = "usuario", schema = "data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // BIGINT en PostgreSQL

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId; // INTEGER en PostgreSQL

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    private Empresa empresa;

    @Column(name = "usuario", nullable = false, length = 255)
    private String usuario; // Nombre de usuario o email

    @Column(name = "clave", nullable = false, length = 255)
    private String clave; // Password encriptado con BCrypt

    @Column(name = "id_empleado")
    private Long idEmpleado; // BIGINT, puede ser null

    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO";

    // Métodos de utilidad
    public boolean isActivo() {
        return "ACTIVO".equals(this.estado);
    }

    public void setActivo(boolean activo) {
        this.estado = activo ? "ACTIVO" : "INACTIVO";
    }

    public boolean hasEmpleado() {
        return idEmpleado != null;
    }

    // Para compatibilidad con Spring Security
    public String getPassword() {
        return this.clave;
    }

    public void setPassword(String password) {
        this.clave = password;
    }

    public String getUsername() {
        return this.usuario;
    }

    public void setUsername(String username) {
        this.usuario = username;
    }

    // Métodos virtuales para la respuesta del API
    @Transient
    public String getNombre() {
        // Extraer nombre del email o retornar el usuario
        if (usuario != null && usuario.contains("@")) {
            return usuario.substring(0, usuario.indexOf("@"));
        }
        return usuario;
    }

    @Transient
    public String getEmail() {
        // Si el usuario es un email, retornarlo
        if (usuario != null && usuario.contains("@")) {
            return usuario;
        }
        return null;
    }

    @Transient
    public Boolean getActivo() {
        return isActivo();
    }

    @Transient
    public Boolean getEsAdmin() {
        // Por ahora, si el usuario contiene "admin", es admin
        // TODO: Implementar sistema de roles completo
        return usuario != null && usuario.toLowerCase().contains("admin");
    }
}
