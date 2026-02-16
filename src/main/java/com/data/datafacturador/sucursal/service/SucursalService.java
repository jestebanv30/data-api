package com.data.datafacturador.sucursal.service;

import com.data.datafacturador.exception.AccesoDenegadoException;
import com.data.datafacturador.repository.UsuarioRepository;
import com.data.datafacturador.sucursal.dto.SucursalDetailResponse;
import com.data.datafacturador.sucursal.dto.SucursalListResponse;
import com.data.datafacturador.sucursal.entity.Sucursal;
import com.data.datafacturador.sucursal.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Sucursales
 * Maneja la lógica de negocio relacionada con sucursales
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;
    private final com.data.datafacturador.repository.EmpresaRepository empresaRepository;
    private final com.data.datafacturador.repository.UsuarioRepository usuarioRepository; // Para validación de usuarios autenticado

    /**
     * Lista todas las sucursales del sistema (Super Admin)
     */
    @Transactional(readOnly = true)
    public Page<SucursalListResponse> listarTodas(Pageable pageable) {
        log.debug("Listando todas las sucursales (Super Admin)");
        return sucursalRepository.findAll(pageable)
                .map(sucursalMapper::toListResponse);
    }

    /**
     * Lista todas las sucursales de la empresa del usuario autenticado
     * Retorna vista resumida
     */
    @Transactional(readOnly = true)
    public List<SucursalListResponse> listarMisSucursales() {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado();

        log.debug("Listando sucursales para empresa: {}", empresaId);

        List<Sucursal> sucursales = sucursalRepository.findByEmpresaId(empresaId);

        log.info("Se encontraron {} sucursales para empresa {}", sucursales.size(), empresaId);

        return sucursales.stream()
                .map(sucursalMapper::toListResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las sucursales de una empresa específica
     * Para uso de Super Admin
     */
    @Transactional(readOnly = true)
    public List<SucursalListResponse> listarSucursalesPorEmpresa(Integer empresaId) {
        log.debug("Listando sucursales (Admin) para empresa: {}", empresaId);

        List<Sucursal> sucursales = sucursalRepository.findByEmpresaId(empresaId);

        log.info("Se encontraron {} sucursales para empresa {}", sucursales.size(), empresaId);

        return sucursales.stream()
                .map(sucursalMapper::toListResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el detalle completo de una sucursal
     */
    @Transactional(readOnly = true)
    public SucursalDetailResponse obtenerDetalleSucursal(Long sucursalId) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado();

        log.debug("Obteniendo detalle de sucursal {} para empresa {}", sucursalId, empresaId);

        Sucursal sucursal = sucursalRepository.findByIdAndEmpresaId(sucursalId, empresaId)
                .orElseThrow(() -> new AccesoDenegadoException(
                        "Sucursal no encontrada o no pertenece a su empresa"));

        log.info("Detalle de sucursal obtenido: {} - {}", sucursal.getId(), sucursal.getNombreComercial());

        return sucursalMapper.toDetailResponse(sucursal);
    }

    /**
     * Obtiene el detalle de una sucursal por ID (Admin)
     * Sin restricción de empresa
     */
    @Transactional(readOnly = true)
    public SucursalDetailResponse obtenerDetalleSucursalPorId(Long sucursalId) {
        log.debug("Obteniendo detalle de sucursal (Admin): {}", sucursalId);

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        return sucursalMapper.toDetailResponse(sucursal);
    }

    /**
     * Crea una nueva sucursal para la empresa del usuario
     */
    @Transactional
    public SucursalDetailResponse crearSucursal(com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado();
        return registrarSucursal(empresaId, request);
    }

    /**
     * Crea una sucursal para una empresa específica (Admin)
     */
    @Transactional
    public SucursalDetailResponse crearSucursalPorEmpresa(Integer empresaId, com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        log.info("Creando sucursal (Admin) para empresa: {}", empresaId);
        return registrarSucursal(empresaId, request);
    }

    private SucursalDetailResponse registrarSucursal(Integer empresaId, com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        log.debug("Creando nueva sucursal para empresa {}", empresaId);

        // Obtener datos de la empresa para heredar valores obligatorios (Razon Social, NIT)
        com.data.datafacturador.entity.Empresa empresa = empresaRepository.findById(Long.valueOf(empresaId))
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada: " + empresaId));
        
        Sucursal sucursal = new Sucursal();
        sucursal.setEmpresaId(empresaId);
        
        // Mapear campos desde el request primero
        mapearDatosSucursal(sucursal, request);
        
        // 1. Heredar Razon Social si no viene en request
        if (sucursal.getNombreRazonSocial() == null || sucursal.getNombreRazonSocial().isEmpty()) {
             sucursal.setNombreRazonSocial(empresa.getRazonSocial());
        }
        
        // 2. Heredar NIT si no viene en request
        if (sucursal.getCcNit() == null || sucursal.getCcNit().isEmpty()) {
            sucursal.setCcNit(empresa.getNit());
        }
        
        // 3. Heredar Ubicación si no viene en request (Opcional, pero útil)
        if (sucursal.getPais() == null) sucursal.setPais(empresa.getPais());
        if (sucursal.getDepartamento() == null) sucursal.setDepartamento(empresa.getDepartamento());
        if (sucursal.getCiudad() == null) sucursal.setCiudad(empresa.getCiudad()); // Nombre ciudad
        if (sucursal.getDireccion() == null) sucursal.setDireccion(empresa.getDireccion());
        
        // 4. Heredar Contacto si no viene
        if (sucursal.getTelefono() == null) sucursal.setTelefono(empresa.getTelefono());
        if (sucursal.getCelular() == null) sucursal.setCelular(empresa.getCelular());
        if (sucursal.getCorreo() == null) sucursal.setCorreo(empresa.getEmail());
        sucursal.setEstado("ACTIVO");
        sucursal.setFechaActualizacion(java.time.ZonedDateTime.now());
        
        // Defaults para evitar errores NOT NULL (misma lógica que en EmpresaService)
        sucursal.setCopiasVentas(1L);
        sucursal.setCopiasCompras(1L);
        sucursal.setCopiasCobros(1L);
        sucursal.setCopiasPagos(1L);
        sucursal.setSincronizarDian(0L);
        sucursal.setLogo(new byte[0]);
        sucursal.setSlogan("Gracias por su compra");
        
        // Mapear campos desde el request
        mapearDatosSucursal(sucursal, request);

        Sucursal guardada = sucursalRepository.save(sucursal);
        log.info("Sucursal creada exitosamente: {}", guardada.getId());

        return sucursalMapper.toDetailResponse(guardada);
    }

    /**
     * Actualiza una sucursal existente
     */
    @Transactional
    public SucursalDetailResponse actualizarSucursal(Long sucursalId, com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        Integer empresaId = obtenerEmpresaIdUsuarioAutenticado();
        
        log.debug("Actualizando sucursal {} para empresa {}", sucursalId, empresaId);
        
        Sucursal sucursal = sucursalRepository.findByIdAndEmpresaId(sucursalId, empresaId)
                .orElseThrow(() -> new AccesoDenegadoException(
                        "Sucursal no encontrada o no pertenece a su empresa"));

        // Actualizar campos
        mapearDatosSucursal(sucursal, request);
        
        sucursal.setFechaActualizacion(java.time.ZonedDateTime.now());
        
        Sucursal actualizada = sucursalRepository.save(sucursal);
        log.info("Sucursal actualizada exitosamente: {}", sucursalId);
        
        return sucursalMapper.toDetailResponse(actualizada);
    }

    private void mapearDatosSucursal(Sucursal sucursal, com.data.datafacturador.sucursal.dto.SucursalRequest request) {
        if (request.getNombreComercial() != null) sucursal.setNombreComercial(request.getNombreComercial());
        if (request.getNombreRazonSocial() != null) sucursal.setNombreRazonSocial(request.getNombreRazonSocial());
        if (request.getCcNit() != null) sucursal.setCcNit(request.getCcNit());
        if (request.getDv() != null) sucursal.setDv(request.getDv());
        
        // Ubicación
        if (request.getPais() != null) sucursal.setPais(request.getPais());
        if (request.getCiudad() != null) sucursal.setIdCiudad(request.getCiudad()); 
        if (request.getDepartamento() != null) sucursal.setDepartamento(request.getDepartamento());
        if (request.getDireccion() != null) sucursal.setDireccion(request.getDireccion());
        if (request.getCiudad() != null) sucursal.setCiudad(request.getCiudad());

        // Contacto
        if (request.getCelular() != null) sucursal.setCelular(request.getCelular());
        if (request.getTelefono() != null) sucursal.setTelefono(request.getTelefono());
        if (request.getCorreo() != null) sucursal.setCorreo(request.getCorreo());

        // Referencias
        if (request.getIdTipoOrganizacion() != null) sucursal.setIdTipoOrganizacion(request.getIdTipoOrganizacion());
        if (request.getIdTipoIdentificacion() != null) sucursal.setIdTipoIdentificacion(request.getIdTipoIdentificacion());
        if (request.getIdResponsabilidadFiscal() != null) sucursal.setIdResponsabilidadFiscal(request.getIdResponsabilidadFiscal());
        if (request.getIdTipoRegimen() != null) sucursal.setIdTipoRegimen(request.getIdTipoRegimen());
        if (request.getIdTipoResponsabilidadTributaria() != null) sucursal.setIdTipoResponsabilidadTributaria(request.getIdTipoResponsabilidadTributaria());

        // Configuración
        if (request.getSlogan() != null) sucursal.setSlogan(request.getSlogan());
        if (request.getSloganOrden() != null) sucursal.setSloganOrden(request.getSloganOrden());
        
        if (request.getCopiasVentas() != null) sucursal.setCopiasVentas(request.getCopiasVentas());
        if (request.getCopiasCompras() != null) sucursal.setCopiasCompras(request.getCopiasCompras());
        if (request.getCopiasCobros() != null) sucursal.setCopiasCobros(request.getCopiasCobros());
        if (request.getCopiasPagos() != null) sucursal.setCopiasPagos(request.getCopiasPagos());
        
        if (request.getSincronizarDian() != null) sucursal.setSincronizarDian(request.getSincronizarDian());
        
        // Documentos y Listas
        if (request.getIdDocumento() != null) sucursal.setIdDocumento(request.getIdDocumento());
        if (request.getIdListaPrecioDefecto() != null) sucursal.setIdListaPrecioDefecto(request.getIdListaPrecioDefecto());
        if (request.getIdTerceroDefecto() != null) sucursal.setIdTerceroDefecto(request.getIdTerceroDefecto());
        
        if (request.getImprimirQr() != null) sucursal.setImprimirQr(request.getImprimirQr());
        if (request.getBuscador() != null) sucursal.setBuscador(request.getBuscador());

        // Logo
        if (request.getLogoBase64() != null && !request.getLogoBase64().isEmpty()) {
            try {
                String base64 = request.getLogoBase64();
                if (base64.contains(",")) {
                    base64 = base64.split(",")[1];
                }
                byte[] decodedBytes = Base64.getDecoder().decode(base64);
                sucursal.setLogo(decodedBytes);
            } catch (IllegalArgumentException e) {
                log.error("Error decodificando logo base64", e);
            }
        }
    }

    /**
     * Obtiene el empresaId del usuario autenticado
     */
    private Integer obtenerEmpresaIdUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return usuarioRepository.findByUsername(username)
                .map(usuario -> usuario.getEmpresaId())
                .orElseThrow(() -> new AccesoDenegadoException("Usuario no encontrado"));
    }
}
