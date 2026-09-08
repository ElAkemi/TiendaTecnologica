package com.tiendatecnologica.enums

/**
 * Estado de una venta. Una venta ANULADA reintegra el stock vendido
 * (ver `VentaService.anularVenta`) pero el registro se conserva
 * en el historial, no se elimina.
 */
enum class EstadoVenta {
    COMPLETADA,
    ANULADA
}