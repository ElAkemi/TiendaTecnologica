package com.tiendatecnologica.model;

import com.tiendatecnologica.enums.TipoMovimiento;

import java.time.LocalDateTime;

/**
 * Movimiento de entrada: suma la cantidad recibida al inventario actual
 * (ej. al recibir una compra a un proveedor).
 */
public class EntradaInventario extends MovimientoInventario {

    /**
     * Crea un movimiento de entrada.
     *
     * @param id identificador único del movimiento
     * @param codigoProducto código del producto que recibe inventario
     * @param cantidad cantidad que ingresa
     * @param fecha fecha y hora del movimiento
     * @param motivo razón de la entrada
     */
    public EntradaInventario(String id, String codigoProducto, int cantidad,
                             LocalDateTime fecha, String motivo) {
        super(id, codigoProducto, cantidad, fecha, motivo);
    }

    /** {@inheritDoc} */
    @Override
    public TipoMovimiento getTipo() {
        return TipoMovimiento.ENTRADA;
    }

    /** {@inheritDoc} */
    @Override
    protected int calcularNuevaCantidad(int cantidadActual) {
        return cantidadActual + getCantidad();
    }
}
