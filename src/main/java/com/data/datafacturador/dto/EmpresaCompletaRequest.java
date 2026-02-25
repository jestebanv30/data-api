package com.data.datafacturador.dto;

import com.data.datafacturador.sucursal.dto.SucursalRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class EmpresaCompletaRequest extends EmpresaRequest {

    /**
     * Detalles específicos para la Sucursal Principal.
     * Si es null, se usarán los datos de la empresa por defecto (comportamiento actual).
     */
    private SucursalRequest detalleSucursalPrincipal;
}
