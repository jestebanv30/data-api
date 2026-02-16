package com.data.datafacturador.controller;

import com.data.datafacturador.dto.EmpresaResponse;
import com.data.datafacturador.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de empresa
 * Endpoints: /api/empresas/*
 */
@Slf4j
@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    /**
     * GET /api/empresas/mi-empresa
     * Obtiene los datos de la empresa del usuario autenticado
     */
    @GetMapping("/mi-empresa")
    public ResponseEntity<Map<String, Object>> obtenerMiEmpresa() {
        log.info("GET /api/empresas/mi-empresa");

        EmpresaResponse empresa = empresaService.obtenerMiEmpresa();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", empresa);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/empresas/mi-empresa
     * Actualiza los datos de la empresa del usuario autenticado
     */
    @PutMapping("/mi-empresa")
    public ResponseEntity<Map<String, Object>> actualizarMiEmpresa(@RequestBody EmpresaResponse request) {
        log.info("PUT /api/empresas/mi-empresa");

        EmpresaResponse empresa = empresaService.actualizarMiEmpresa(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Empresa actualizada exitosamente");
        response.put("data", empresa);

        return ResponseEntity.ok(response);
    }
}
