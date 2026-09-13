package com.tiendatecnologica.enums;

/**
 * Representa los posibles estados de una venta.
 *
 * Una venta completada puede ser anulada posteriormente.
 * Al anular una venta, se reintegra al inventario la cantidad
 * de productos que había sido vendida, pero el registro de la
 * venta se conserva en el historial.
 */
public enum EstadoVenta {

    /** La venta fue realizada y se encuentra vigente. */
    COMPLETADA,

    /** La venta fue anulada y los productos vendidos fueron reintegrados al inventario. */
    ANULADA
}