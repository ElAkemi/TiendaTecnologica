package com.tiendatecnologica.enums;

/**
 * Representa los posibles estados de una orden de compra.
 *
 * Una orden comienza en estado PENDIENTE y posteriormente puede
 * pasar a RECIBIDA o CANCELADA.
 */
public enum EstadoOrdenCompra {

    /** La orden fue registrada y está pendiente de ser recibida. */
    PENDIENTE,

    /** La orden fue recibida y sus productos fueron ingresados al inventario. */
    RECIBIDA,

    /** La orden fue cancelada y no modifica el inventario. */
    CANCELADA
}