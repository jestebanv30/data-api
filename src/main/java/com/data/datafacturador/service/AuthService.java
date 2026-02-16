package com.data.datafacturador.service;

import com.data.datafacturador.dto.LoginRequest;
import com.data.datafacturador.dto.RefreshTokenRequest;
import com.data.datafacturador.dto.LoginResponse;
import com.data.datafacturador.dto.UsuarioResponse;
import com.data.datafacturador.service.UsuarioMapper;
import com.data.datafacturador.exception.CredencialesInvalidasException;
import com.data.datafacturador.security.JwtTokenProvider;
import com.data.datafacturador.entity.Usuario;
import com.data.datafacturador.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación
 * Maneja login, refresh token y logout
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Autentica un usuario y genera tokens JWT
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            log.info("Intento de login para usuario: {}", request.getUsername());

            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener usuario de la base de datos
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

            // Verificar que el usuario esté activo (campo estado)
            if (!"ACTIVO".equals(usuario.getEstado())) {
                throw new CredencialesInvalidasException("Usuario inactivo. Contacte al administrador.");
            }

            // Verificar que el usuario y su empresa estén activos (campo activo legacy)
            if (!usuario.getActivo()) {
                throw new CredencialesInvalidasException("Usuario inactivo");
            }

            if (usuario.getEmpresa() == null || !usuario.getEmpresa().getActivo()) {
                throw new CredencialesInvalidasException("Empresa inactiva");
            }

            Long empresaId = usuario.getEmpresa().getId();

            // Generar tokens
            String accessToken = jwtTokenProvider.generateAccessToken(authentication, empresaId);
            String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername(), empresaId);

            // Mapear usuario a DTO
            UsuarioResponse usuarioResponse = usuarioMapper.toResponse(usuario);

            log.info("Login exitoso para usuario: {} - Empresa: {}", request.getUsername(), empresaId);

            return LoginResponse.builder()
                    .success(true)
                    .message("Login exitoso")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpiration / 1000) // Convertir a segundos
                    .usuario(usuarioResponse)
                    .build();

        } catch (AuthenticationException ex) {
            log.error("Error de autenticación para usuario: {}", request.getUsername(), ex);
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }
    }

    /**
     * Refresca el access token usando un refresh token válido
     */
    @Transactional(readOnly = true)
    public Map<String, Object> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validar refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                throw new CredencialesInvalidasException("Refresh token inválido o expirado");
            }

            // Verificar que sea un refresh token
            if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
                throw new CredencialesInvalidasException("Token no es un refresh token");
            }

            // Extraer información del token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            Long empresaId = jwtTokenProvider.getEmpresaIdFromToken(refreshToken);

            // Verificar que el usuario siga existiendo y esté activo
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

            if (!usuario.getActivo() || usuario.getEmpresa() == null || !usuario.getEmpresa().getActivo()) {
                throw new CredencialesInvalidasException("Usuario o empresa inactivos");
            }

            // Crear autenticación para generar nuevo access token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username, null, null);

            // Generar nuevo access token
            String newAccessToken = jwtTokenProvider.generateAccessToken(authentication, empresaId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtExpiration / 1000);

            log.info("Token refrescado para usuario: {}", username);

            return response;

        } catch (Exception ex) {
            log.error("Error al refrescar token", ex);
            throw new CredencialesInvalidasException("Error al refrescar token: " + ex.getMessage());
        }
    }

    /**
     * Cierra la sesión del usuario
     * TODO: Implementar blacklist de tokens con Redis
     */
    public Map<String, Object> logout() {
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout exitoso");

        log.info("Logout exitoso");

        return response;
    }
}
