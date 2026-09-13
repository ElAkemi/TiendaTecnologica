package com.tiendatecnologica.enums;

/**
 * Representa los tipos de movimientos que pueden realizarse
 * sobre el inventario de productos.
 *
 * Permite identificar si un movimiento aumenta, disminuye
 * o modifica directamente la cantidad disponible.
 */
public enum TipoMovimiento {

    /** Movimiento que aumenta la cantidad disponible del producto. */
    ENTRADA,

    /** Movimiento que disminuye la cantidad disponible del producto. */
    SALIDA,

    /** Movimiento que establece una nueva cantidad en el inventario. */
    AJUSTE
}