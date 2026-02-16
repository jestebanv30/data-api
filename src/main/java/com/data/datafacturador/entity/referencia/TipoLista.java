package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de referencia - Tipo de Lista de Precios
 * Tabla data2.tipo_lista
 */
@Entity
@Table(name = "tipo_lista", schema = "data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoLista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_lista", nullable = false)
    private String nombre; // Mapped to 'nombre' conceptually
}
