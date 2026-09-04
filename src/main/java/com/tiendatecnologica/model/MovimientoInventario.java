package com.tiendatecnologica.model;

import com.tiendatecnologica.enums.TipoMovimiento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un movimiento de inventario (entrada, salida o ajuste).
 * Es una clase abstracta: cada subtipo concreto ({@link EntradaInventario},
 * {@link SalidaInventario}, {@link AjusteInventario}) sabe calcular la nueva
 * cantidad de inventario a partir de la cantidad actual, evitando repetir
 * esa lógica (antes vivía en un switch dentro del servicio).
 */
public abstract class MovimientoInventario {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String id;
    private final String codigoProducto;
    private int cantidad;
    private int cantidadAnterior;
    private int cantidadNueva;
    private final LocalDateTime fecha;
    private final String motivo;

    /**
     * Crea un movimiento de inventario.
     *
     * @param id identificador único del movimiento (ej. MOV-00001)
     * @param codigoProducto código del producto afectado
     * @param cantidad cantidad involucrada en el movimiento
     * @param fecha fecha y hora en que ocurrió
     * @param motivo razón del movimiento (ej. "Venta V-00003")
     */
    protected MovimientoInventario(String id, String codigoProducto, int cantidad,
                                   LocalDateTime fecha, String motivo) {
        this.id = id;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.motivo = motivo;
    }

    /**
     * Aplica este movimiento sobre la cantidad actual de un producto,
     * calculando y registrando (en el propio movimiento) la cantidad
     * anterior y la nueva, para dejar rastro en el historial.
     *
     * @param cantidadActual cantidad disponible del producto antes del movimiento
     * @return la nueva cantidad disponible después de aplicar el movimiento
     */
    public int aplicar(int cantidadActual) {
        this.cantidadAnterior = cantidadActual;
        this.cantidadNueva = calcularNuevaCantidad(cantidadActual);
        return this.cantidadNueva;
    }

    /**
     * Calcula la nueva cantidad de inventario según el tipo de movimiento.
     * Cada subclase define su propia regla (sumar, restar, o fijar un valor).
     *
     * @param cantidadActual cantidad disponible antes del movimiento
     * @return la cantidad disponible después del movimiento
     */
    protected abstract int calcularNuevaCantidad(int cantidadActual);

    /**
     * @return el tipo de movimiento (ENTRADA, SALIDA o AJUSTE)
     */
    public abstract TipoMovimiento getTipo();

    /**
     * Fija directamente los valores de cantidad anterior/nueva, sin
     * recalcularlos. Se usa al reconstruir un movimiento ya existente desde
     * el CSV, donde esos valores ya fueron calculados en su momento.
     *
     * @param cantidadAnterior cantidad disponible antes del movimiento
     * @param cantidadNueva cantidad disponible después del movimiento
     */
    public void fijarHistorial(int cantidadAnterior, int cantidadNueva) {
        this.cantidadAnterior = cantidadAnterior;
        this.cantidadNueva = cantidadNueva;
    }

    /**
     * @return el identificador único del movimiento
     */
    public String getId() {
        return id;
    }

    /**
     * @return el código del producto afectado
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * @return la cantidad involucrada en el movimiento
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Permite a las subclases ajustar la cantidad involucrada (ej. el ajuste
     * la recalcula como la diferencia contra el valor anterior).
     *
     * @param cantidad nueva cantidad a registrar
     */
    protected void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return la cantidad disponible antes del movimiento
     */
    public int getCantidadAnterior() {
        return cantidadAnterior;
    }

    /**
     * @return la cantidad disponible después del movimiento
     */
    public int getCantidadNueva() {
        return cantidadNueva;
    }

    /**
     * @return la fecha y hora en que ocurrió el movimiento
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * @return la razón del movimiento
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Convierte el movimiento a una fila lista para escribir en movimientos.csv.
     * Encabezado: id,codigoProducto,tipo,cantidad,cantidadAnterior,cantidadNueva,fecha,motivo
     *
     * @return arreglo de strings con los valores de cada columna, en orden
     */
    public String[] toCsv() {
        return new String[] {
                id, codigoProducto, getTipo().name(), String.valueOf(cantidad),
                String.valueOf(cantidadAnterior), String.valueOf(cantidadNueva),
                fecha.format(FORMATO_FECHA), motivo == null ? "" : motivo
        };
    }

    /**
     * @return una representación legible del movimiento, útil para depurar
     */
    @Override
    public String toString() {
        return getTipo() + " " + id + " - producto " + codigoProducto
                + ": " + cantidadAnterior + " -> " + cantidadNueva;
    }
}
