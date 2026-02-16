package com.data.datafacturador.service;

import com.data.datafacturador.dto.EmpleadoListaDTO;
import com.data.datafacturador.dto.EmpleadoSinUsuarioDTO;
import com.data.datafacturador.entity.nomina.Empleado;
import com.data.datafacturador.repository.nomina.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    
    // Referencias Globales
    private final com.data.datafacturador.repository.referencia.TipoIdentificacionRepository tipoIdentificacionRepository;
    private final com.data.datafacturador.repository.referencia.TipoContratoRepository tipoContratoRepository;
    private final com.data.datafacturador.repository.referencia.TipoPeriodoRepository tipoPeriodoRepository;
    private final com.data.datafacturador.repository.referencia.ConceptoRetiroRepository conceptoRetiroRepository;
    private final com.data.datafacturador.repository.referencia.TipoCargoRepository tipoCargoRepository;
    private final com.data.datafacturador.repository.referencia.TipoTrabajadorRepository tipoTrabajadorRepository;
    private final com.data.datafacturador.repository.referencia.SubtipoCotizanteRepository subtipoCotizanteRepository;
    private final com.data.datafacturador.repository.referencia.FondoSaludRepository fondoSaludRepository;
    private final com.data.datafacturador.repository.referencia.FondoPensionRepository fondoPensionRepository;
    private final com.data.datafacturador.repository.referencia.FondoCesantiasRepository fondoCesantiasRepository;
    private final com.data.datafacturador.repository.referencia.ArlRepository arlRepository;
    private final com.data.datafacturador.repository.referencia.CategoriaArlRepository categoriaArlRepository;
    private final com.data.datafacturador.repository.referencia.CajaCompensacionRepository cajaCompensacionRepository;

    // Referencias Empresa
    private final com.data.datafacturador.repository.nomina.BancoRepository bancoRepository;
    private final com.data.datafacturador.repository.nomina.FormaPagoRepository formaPagoRepository;
    private final com.data.datafacturador.repository.nomina.MedioPagoRepository medioPagoRepository;

    // Referencias Usuario (para eliminación y sincronización)
    private final com.data.datafacturador.repository.UsuarioRepository usuarioRepository;
    private final com.data.datafacturador.repository.PermisoUsuarioRepository permisoUsuarioRepository;

    @Transactional(readOnly = true)
    public Page<Empleado> listarEmpleados(Integer empresaId, Pageable pageable) {
        return empleadoRepository.findByEmpresaId(empresaId, pageable);
    }

    @Transactional(readOnly = true)
    public Empleado obtenerEmpleado(Long id, Integer empresaId) {
        return empleadoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado o no pertenece a la empresa."));
    }

    @Transactional(readOnly = true)
    public List<EmpleadoSinUsuarioDTO> listarEmpleadosSinUsuario(Integer empresaId) {
        return empleadoRepository.findEmpleadosSinUsuario(empresaId).stream()
                .map(empleado -> new EmpleadoSinUsuarioDTO(
                        empleado.getId(),
                        empleado.getNombres(),
                        empleado.getApellidos(),
                        empleado.getCc()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpleadoListaDTO> listarEmpleadosLista(Integer empresaId) {
        return empleadoRepository.findByEmpresaId(empresaId).stream()
                .map(empleado -> {
                    EmpleadoListaDTO.CargoDTO cargoDTO = null;
                    if (empleado.getCargo() != null) {
                        cargoDTO = new EmpleadoListaDTO.CargoDTO(empleado.getCargo().getNombre());
                    }
                    
                    return new EmpleadoListaDTO(
                            empleado.getId(),
                            empleado.getNombres(),
                            empleado.getApellidos(),
                            empleado.getCc(),
                            cargoDTO,
                            empleado.getEstado(),
                            empleado.getFechaInicio() != null ? empleado.getFechaInicio().toString() : null
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Empleado crearEmpleado(Empleado empleado, Integer empresaId) {
        empleado.setEmpresaId(empresaId);
        
        // Validaciones
        if (empleadoRepository.existsByCcAndEmpresaId(empleado.getCc(), empresaId)) {
            throw new IllegalArgumentException("Ya existe un empleado con la cédula " + empleado.getCc());
        }
        // Correo validation optional?
        if (empleado.getCorreo() != null && !empleado.getCorreo().isEmpty() && 
            empleadoRepository.existsByCorreoAndEmpresaId(empleado.getCorreo(), empresaId)) {
             throw new IllegalArgumentException("Ya existe un empleado con el correo " + empleado.getCorreo());
        }

        empleado.setEstado("ACTIVO");
        empleado.setFechaActualizacion(ZonedDateTime.now());
        hidratarReferencias(empleado, empresaId);
        return empleadoRepository.save(empleado);
    }

    @Transactional
    public void eliminarEmpleado(Long id, Integer empresaId) {
        Empleado empleado = obtenerEmpleado(id, empresaId);
        
        // 1. Si tiene usuario asociado, eliminarlo (Usuario + Permisos)
        if (empleado.getUsuario() != null) {
            Long usuarioId = empleado.getUsuario().getId();
            
            // Eliminar permisos primero
            permisoUsuarioRepository.deleteByIdUsuario(usuarioId);
            
            // Eliminar usuario
            usuarioRepository.deleteById(usuarioId);
        }

        // 2. Eliminar empleado físicamente
        empleadoRepository.delete(empleado);
    }

    @Transactional
    public void cambiarEstadoEmpleado(Long id, String nuevoEstado, Integer empresaId) {
        Empleado empleado = obtenerEmpleado(id, empresaId);
        
        if (!"ACTIVO".equals(nuevoEstado) && !"INACTIVO".equals(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
        }

        // Si el estado es el mismo, no hacer nada
        if (nuevoEstado.equals(empleado.getEstado())) {
            return;
        }

        // 1. Actualizar estado del empleado
        empleado.setEstado(nuevoEstado);
        empleado.setFechaActualizacion(ZonedDateTime.now());
        empleadoRepository.save(empleado);

        // 2. Sincronizar usuario asociado
        if (empleado.getUsuario() != null) {
            empleado.getUsuario().setEstado(nuevoEstado);
            usuarioRepository.save(empleado.getUsuario());
        }
    }

    @Transactional
    public Empleado actualizarEmpleado(Long id, Empleado empleadoDetalles, Integer empresaId) {
        Empleado empleado = obtenerEmpleado(id, empresaId);

        // Validar unicidad si cambia CC
        if (!empleado.getCc().equals(empleadoDetalles.getCc()) && 
            empleadoRepository.existsByCcAndEmpresaId(empleadoDetalles.getCc(), empresaId)) {
            throw new IllegalArgumentException("Ya existe un empleado con la cédula " + empleadoDetalles.getCc());
        }

        // Update fields only if not null (PATCH-like behavior for safety)
        if (empleadoDetalles.getNombres() != null) empleado.setNombres(empleadoDetalles.getNombres());
        if (empleadoDetalles.getApellidos() != null) empleado.setApellidos(empleadoDetalles.getApellidos());
        if (empleadoDetalles.getTipoIdentificacion() != null) empleado.setTipoIdentificacion(empleadoDetalles.getTipoIdentificacion());
        if (empleadoDetalles.getCc() != null) empleado.setCc(empleadoDetalles.getCc());
        if (empleadoDetalles.getDireccion() != null) empleado.setDireccion(empleadoDetalles.getDireccion());
        if (empleadoDetalles.getCorreo() != null) empleado.setCorreo(empleadoDetalles.getCorreo());
        if (empleadoDetalles.getCelular() != null) empleado.setCelular(empleadoDetalles.getCelular());
        if (empleadoDetalles.getTelefono() != null) empleado.setTelefono(empleadoDetalles.getTelefono());
        if (empleadoDetalles.getPais() != null) empleado.setPais(empleadoDetalles.getPais());
        if (empleadoDetalles.getIdCiudad() != null) empleado.setIdCiudad(empleadoDetalles.getIdCiudad());
        if (empleadoDetalles.getCiudad() != null) empleado.setCiudad(empleadoDetalles.getCiudad());
        if (empleadoDetalles.getDepartamento() != null) empleado.setDepartamento(empleadoDetalles.getDepartamento());
        
        if (empleadoDetalles.getFechaInicio() != null) empleado.setFechaInicio(empleadoDetalles.getFechaInicio());
        if (empleadoDetalles.getFechaFinalizacion() != null) empleado.setFechaFinalizacion(empleadoDetalles.getFechaFinalizacion());
        if (empleadoDetalles.getNominaElectronica() != null) empleado.setNominaElectronica(empleadoDetalles.getNominaElectronica());
        
        if (empleadoDetalles.getTipoContrato() != null) empleado.setTipoContrato(empleadoDetalles.getTipoContrato());
        if (empleadoDetalles.getTipoPeriodo() != null) empleado.setTipoPeriodo(empleadoDetalles.getTipoPeriodo());
        if (empleadoDetalles.getConceptoRetiro() != null) empleado.setConceptoRetiro(empleadoDetalles.getConceptoRetiro());
        if (empleadoDetalles.getFormaPago() != null) empleado.setFormaPago(empleadoDetalles.getFormaPago());
        if (empleadoDetalles.getMedioPago() != null) empleado.setMedioPago(empleadoDetalles.getMedioPago());
        if (empleadoDetalles.getBanco() != null) empleado.setBanco(empleadoDetalles.getBanco());
        
        if (empleadoDetalles.getNumeroCuenta() != null) empleado.setNumeroCuenta(empleadoDetalles.getNumeroCuenta());
        if (empleadoDetalles.getTipoCuenta() != null) empleado.setTipoCuenta(empleadoDetalles.getTipoCuenta());
        if (empleadoDetalles.getSueldo() != null) empleado.setSueldo(empleadoDetalles.getSueldo());
        if (empleadoDetalles.getSueldoIntegral() != null) empleado.setSueldoIntegral(empleadoDetalles.getSueldoIntegral());
        if (empleadoDetalles.getDeduccionRetencion() != null) empleado.setDeduccionRetencion(empleadoDetalles.getDeduccionRetencion());
        if (empleadoDetalles.getNumeroContrato() != null) empleado.setNumeroContrato(empleadoDetalles.getNumeroContrato());
        if (empleadoDetalles.getCentroCosto() != null) empleado.setCentroCosto(empleadoDetalles.getCentroCosto());
        
        if (empleadoDetalles.getCargo() != null) empleado.setCargo(empleadoDetalles.getCargo());
        if (empleadoDetalles.getTipoTrabajador() != null) empleado.setTipoTrabajador(empleadoDetalles.getTipoTrabajador());
        if (empleadoDetalles.getSubtipoCotizante() != null) empleado.setSubtipoCotizante(empleadoDetalles.getSubtipoCotizante());
        
        if (empleadoDetalles.getFondoSalud() != null) empleado.setFondoSalud(empleadoDetalles.getFondoSalud());
        if (empleadoDetalles.getPorcentajeSalud() != null) empleado.setPorcentajeSalud(empleadoDetalles.getPorcentajeSalud());
        if (empleadoDetalles.getFondoPension() != null) empleado.setFondoPension(empleadoDetalles.getFondoPension());
        if (empleadoDetalles.getPorcentajePension() != null) empleado.setPorcentajePension(empleadoDetalles.getPorcentajePension());
        
        if (empleadoDetalles.getArl() != null) empleado.setArl(empleadoDetalles.getArl());
        if (empleadoDetalles.getCategoriaArl() != null) empleado.setCategoriaArl(empleadoDetalles.getCategoriaArl()); 
        
        if (empleadoDetalles.getCajaCompensacion() != null) empleado.setCajaCompensacion(empleadoDetalles.getCajaCompensacion());
        if (empleadoDetalles.getFondoCesantias() != null) empleado.setFondoCesantias(empleadoDetalles.getFondoCesantias());
        
        if (empleadoDetalles.getSucursal() != null) empleado.setSucursal(empleadoDetalles.getSucursal());
        if (empleadoDetalles.getUsuario() != null) empleado.setUsuario(empleadoDetalles.getUsuario());

        // Sincronizar estado con usuario si existe y cambia
        if (empleado.getUsuario() != null && empleadoDetalles.getEstado() != null) {
             if (!empleadoDetalles.getEstado().equals(empleado.getEstado())) {
                 empleado.getUsuario().setEstado(empleadoDetalles.getEstado());
                 usuarioRepository.save(empleado.getUsuario());
             }
        }
        
        if (empleadoDetalles.getEstado() != null) {
            empleado.setEstado(empleadoDetalles.getEstado());
        }

        empleado.setFechaActualizacion(ZonedDateTime.now());
        
        hidratarReferencias(empleado, empresaId);

        return empleadoRepository.save(empleado);
    }
    
    private void hidratarReferencias(Empleado e, Integer empresaId) {
        // --- Referencias por ID (PK) ---
        if (e.getTipoIdentificacion() != null && e.getTipoIdentificacion().getId() != null) {
            e.setTipoIdentificacion(tipoIdentificacionRepository.findById(e.getTipoIdentificacion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo Identificación no válido")));
        }
        if (e.getTipoPeriodo() != null && e.getTipoPeriodo().getId() != null) {
            e.setTipoPeriodo(tipoPeriodoRepository.findById(e.getTipoPeriodo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo Periodo no válido")));
        }
        if (e.getConceptoRetiro() != null && e.getConceptoRetiro().getId() != null) {
            e.setConceptoRetiro(conceptoRetiroRepository.findById(e.getConceptoRetiro().getId())
                .orElseThrow(() -> new IllegalArgumentException("Concepto Retiro no válido")));
        }
        
        // --- Referencias que antes eran por Código y ahora son por ID (Schema Sync) ---
        if (e.getCargo() != null && e.getCargo().getId() != null) {
            e.setCargo(tipoCargoRepository.findById(e.getCargo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cargo no válido")));
        }
        if (e.getSubtipoCotizante() != null && e.getSubtipoCotizante().getId() != null) {
            e.setSubtipoCotizante(subtipoCotizanteRepository.findById(e.getSubtipoCotizante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Subtipo Cotizante no válido")));
        }
        if (e.getFondoSalud() != null && e.getFondoSalud().getId() != null) {
            e.setFondoSalud(fondoSaludRepository.findById(e.getFondoSalud().getId())
                .orElseThrow(() -> new IllegalArgumentException("Fondo Salud no válido")));
        }
        if (e.getFondoPension() != null && e.getFondoPension().getId() != null) {
            e.setFondoPension(fondoPensionRepository.findById(e.getFondoPension().getId())
                .orElseThrow(() -> new IllegalArgumentException("Fondo Pensión no válido")));
        }
        if (e.getFondoCesantias() != null && e.getFondoCesantias().getId() != null) {
            e.setFondoCesantias(fondoCesantiasRepository.findById(e.getFondoCesantias().getId())
                .orElseThrow(() -> new IllegalArgumentException("Fondo Cesantías no válido")));
        }
        if (e.getArl() != null && e.getArl().getId() != null) {
            e.setArl(arlRepository.findById(e.getArl().getId())
                .orElseThrow(() -> new IllegalArgumentException("ARL no válida")));
        }
        if (e.getCategoriaArl() != null && e.getCategoriaArl().getId() != null) {
            e.setCategoriaArl(categoriaArlRepository.findById(e.getCategoriaArl().getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría ARL no válida")));
        }
        if (e.getCajaCompensacion() != null && e.getCajaCompensacion().getId() != null) {
            e.setCajaCompensacion(cajaCompensacionRepository.findById(e.getCajaCompensacion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Caja Compensación no válida")));
        }

        // --- Referencias por Empresa (ID) ---
        if (e.getBanco() != null && e.getBanco().getId() != null) {
            e.setBanco(bancoRepository.findById(e.getBanco().getId())
                .orElseThrow(() -> new IllegalArgumentException("Banco no válido")));
        }
        if (e.getFormaPago() != null && e.getFormaPago().getId() != null) {
            e.setFormaPago(formaPagoRepository.findById(e.getFormaPago().getId())
                .orElseThrow(() -> new IllegalArgumentException("Forma Pago no válida")));
        }
        if (e.getMedioPago() != null && e.getMedioPago().getId() != null) {
            e.setMedioPago(medioPagoRepository.findById(e.getMedioPago().getId())
                .orElseThrow(() -> new IllegalArgumentException("Medio Pago no válido")));
        }

        // --- Referencias por Código DIAN (String) ---
        if (e.getTipoContrato() != null && e.getTipoContrato().getCodigo() != null) {
            e.setTipoContrato(tipoContratoRepository.findByCodigo(e.getTipoContrato().getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Tipo Contrato no válido: " + e.getTipoContrato().getCodigo())));
        }
        if (e.getTipoTrabajador() != null && e.getTipoTrabajador().getCodigo() != null) {
            e.setTipoTrabajador(tipoTrabajadorRepository.findByCodigo(e.getTipoTrabajador().getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Tipo Trabajador no válido: " + e.getTipoTrabajador().getCodigo())));
        }
    }
}
