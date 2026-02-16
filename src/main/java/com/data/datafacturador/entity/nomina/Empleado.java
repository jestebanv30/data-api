package com.data.datafacturador.entity.nomina;

import com.data.datafacturador.entity.Usuario;
import com.data.datafacturador.sucursal.entity.Sucursal;
import com.data.datafacturador.entity.referencia.*;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "empleado", schema = "data2")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;

    @ManyToOne
    @JoinColumn(name = "id_tipo_identificacion")
    private TipoIdentificacion tipoIdentificacion;

    private String cc; // Numero documento
    private String direccion;
    private String correo;
    private String celular;
    private String telefono;
    private String pais;

    @Column(name = "id_ciudad")
    private String idCiudad; // Keeping as String for now as direct relation to CiudadDepartamento is complex with type mismatch

    private String ciudad;
    private String departamento;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    @Column(name = "nomina_electronica")
    private String nominaElectronica;

    // References via Code (Varchar in DB)
    @ManyToOne
    @JoinColumn(name = "id_tipo_contrato", referencedColumnName = "codigo_dian")
    private TipoContrato tipoContrato;

    // References via ID (BigInt in DB)
    @ManyToOne
    @JoinColumn(name = "id_tipo_periodo")
    private TipoPeriodo tipoPeriodo;

    @ManyToOne
    @JoinColumn(name = "id_concepto_retiro")
    private ConceptoRetiro conceptoRetiro;

    @ManyToOne
    @JoinColumn(name = "id_forma_pago")
    private FormaPago formaPago;

    @ManyToOne
    @JoinColumn(name = "id_medio_pago")
    private MedioPago medioPago;

    @ManyToOne
    @JoinColumn(name = "id_banco")
    private Banco banco;

    @Column(name = "numero_cuenta")
    private String numeroCuenta;

    @Column(name = "tipo_cuenta")
    private String tipoCuenta;

    private BigDecimal sueldo;

    @Column(name = "sueldo_integral")
    private String sueldoIntegral;

    @Column(name = "deduccion_retencion")
    private String deduccionRetencion;

    @Column(name = "numero_contrato")
    private String numeroContrato;

    @Column(name = "centro_costo")
    private String centroCosto;

    // References via Code
    @ManyToOne
    @JoinColumn(name = "id_cargo")
    private TipoCargo cargo;

    @ManyToOne
    @JoinColumn(name = "id_tipo_trabajador", referencedColumnName = "codigo_dian")
    private TipoTrabajador tipoTrabajador;

    @ManyToOne
    @JoinColumn(name = "id_subtipo_cotizante")
    private SubtipoCotizante subtipoCotizante;

    @ManyToOne
    @JoinColumn(name = "id_fondo_salud")
    private FondoSalud fondoSalud;

    @Column(name = "porcentaje_salud")
    private String porcentajeSalud;

    @ManyToOne
    @JoinColumn(name = "id_fondo_pension")
    private FondoPension fondoPension;

    @Column(name = "porcentaje_pension")
    private String porcentajePension;

    @ManyToOne
    @JoinColumn(name = "id_arl")
    private Arl arl;

    @ManyToOne
    @JoinColumn(name = "id_porcentaje_arl")
    private CategoriaArl categoriaArl;

    @ManyToOne
    @JoinColumn(name = "id_caja_compensacion")
    private CajaCompensacion cajaCompensacion;

    @ManyToOne
    @JoinColumn(name = "id_fondo_cesantias")
    private FondoCesantias fondoCesantias;

    private String estado;

    @Column(name = "fecha_actualizacion")
    private ZonedDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "empresa_id", nullable = false)
    private Integer empresaId;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (fechaActualizacion == null) {
            fechaActualizacion = ZonedDateTime.now();
        }
    }
}
