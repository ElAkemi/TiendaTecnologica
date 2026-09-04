package com.tiendatecnologica.model;

import com.tiendatecnologica.enums.TipoMovimiento;

import java.time.LocalDateTime;

/**
 * Movimiento de salida: resta la cantidad indicada del inventario actual
 * (ej. al confirmar una venta). Valida que exista suficiente stock antes
 * de restar.
 */
public class SalidaInventario extends MovimientoInventario {

    /**
     * Crea un movimiento de salida.
     *
     * @param id identificador único del movimiento
     * @param codigoProducto código del producto que pierde inventario
     * @param cantidad cantidad que sale
     * @param fecha fecha y hora del movimiento
     * @param motivo razón de la salida
     */
    public SalidaInventario(String id, String codigoProducto, int cantidad,
                            LocalDateTime fecha, String motivo) {
        super(id, codigoProducto, cantidad, fecha, motivo);
    }

    /** {@inheritDoc} */
    @Override
    public TipoMovimiento getTipo() {
        return TipoMovimiento.SALIDA;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException si no hay suficiente stock disponible
     */
    @Override
    protected int calcularNuevaCantidad(int cantidadActual) {
        if (cantidadActual < getCantidad()) {
            throw new IllegalStateException(
                    "Stock insuficiente: disponible " + cantidadActual + ", solicitado " + getCantidad());
        }
        return cantidadActual - getCantidad();
    }
}
