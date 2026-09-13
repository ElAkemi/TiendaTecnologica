package com.tiendatecnologica.enums;

/**
 * Representa los posibles estados de una devolución.
 *
 * El estado permite controlar las diferentes etapas por las que
 * puede pasar una solicitud de devolución desde que es registrada
 * hasta que finaliza el proceso.
 */
public enum EstadoDevolucion {

    /** La devolución fue registrada y está pendiente de ser atendida. */
    PENDIENTE,

    /** La devolución está siendo evaluada para determinar si procede. */
    EN_REVISION,

    /** La devolución fue evaluada y se determinó que procede. */
    APROBADA,

    /** La devolución fue evaluada y se determinó que no procede. */
    RECHAZADA,

    /** El proceso de devolución fue completado. */
    FINALIZADA
}