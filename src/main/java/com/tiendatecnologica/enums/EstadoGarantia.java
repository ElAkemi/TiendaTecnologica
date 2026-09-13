package com.tiendatecnologica.enums;

/**
 * Representa los posibles estados de una garantía.
 *
 * El estado permite controlar las diferentes etapas por las que
 * puede pasar una solicitud de garantía desde que es registrada
 * hasta que finaliza el proceso.
 */
public enum EstadoGarantia {

    /** La garantía fue registrada y está pendiente de ser atendida. */
    PENDIENTE,

    /** La garantía está siendo evaluada para determinar si procede. */
    EN_REVISION,

    /** La garantía fue evaluada y se determinó que procede. */
    APROBADA,

    /** La garantía fue evaluada y se determinó que no procede. */
    RECHAZADA,

    /** El proceso de garantía fue completado. */
    FINALIZADA
}