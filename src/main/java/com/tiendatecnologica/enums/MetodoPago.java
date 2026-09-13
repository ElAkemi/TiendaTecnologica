package com.tiendatecnologica.enums;

/**
 * Representa los métodos de pago disponibles en el sistema.
 *
 * Permite identificar la forma en que se realizó el pago
 * de una venta.
 */
public enum MetodoPago {

    /** Pago realizado en efectivo. */
    EFECTIVO,

    /** Pago realizado mediante tarjeta. */
    TARJETA,

    /** Pago realizado mediante SINPE. */
    SINPE
}