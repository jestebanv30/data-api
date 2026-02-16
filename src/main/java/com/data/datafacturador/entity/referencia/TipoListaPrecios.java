package com.data.datafacturador.entity.referencia;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_lista", schema = "data2")
public class TipoListaPrecios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_lista")
    private String nombre;

    @Column(name = "id_sucursal")
    private Long idSucursal;
    
    // We can ignore fecha_creacion and id_usuario for the reference list
}
