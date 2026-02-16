package com.data.datafacturador.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando una empresa no es encontrada
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmpresaNoEncontradaException extends RuntimeException {

    public EmpresaNoEncontradaException(String message) {
        super(message);
    }

    public EmpresaNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmpresaNoEncontradaException(Long id) {
        super("Empresa no encontrada con ID: " + id);
    }
}
