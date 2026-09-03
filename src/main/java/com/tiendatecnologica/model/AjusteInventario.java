package com.tiendatecnologica.model;

import com.tiendatecnologica.enums.TipoMovimiento;

import java.time.LocalDateTime;

/**
 * Movimiento de ajuste: fija el inventario en un valor exacto (ej. tras un
 * conteo físico), sin importar el valor anterior.
 */
public class AjusteInventario extends MovimientoInventario {

    private final int cantidadObjetivo;

    /**
     * Crea un movimiento de ajuste.
     *
     * @param id identificador único del movimiento
     * @param codigoProducto código del producto a ajustar
     * @param cantidadObjetivo cantidad final que debe quedar en inventario
     * @param fecha fecha y hora del movimiento
     * @param motivo razón del ajuste
     */
    public AjusteInventario(String id, String codigoProducto, int cantidadObjetivo,
                            LocalDateTime fecha, String motivo) {
        super(id, codigoProducto, 0, fecha, motivo);
        this.cantidadObjetivo = cantidadObjetivo;
    }

    /** {@inheritDoc} */
    @Override
    public TipoMovimiento getTipo() {
        return TipoMovimiento.AJUSTE;
    }

    /** {@inheritDoc} */
    @Override
    protected int calcularNuevaCantidad(int cantidadActual) {
        setCantidad(Math.abs(cantidadObjetivo - cantidadActual));
        return cantidadObjetivo;
    }
}
