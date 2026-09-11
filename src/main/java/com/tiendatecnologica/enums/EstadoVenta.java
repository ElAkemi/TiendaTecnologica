package com.tiendatecnologica.enums;

/**
 * Estado de una venta. Una venta ANULADA reintegra el stock vendido
 * (ver {@code VentaService.anularVenta}) pero el registro se conserva
 * en el historial, no se elimina.
 */
public enum EstadoVenta {
    COMPLETADA,
    ANULADA
}
