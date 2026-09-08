package com.tiendatecnologica.model;

import com.tiendatecnologica.enums.MetodoPago;

/**
 * Representa el pago de una venta: método utilizado, monto recibido y
 * vuelto a entregar
 * así que sus datos se guardan como columnas dentro de {@code ventas.csv}
 * (ver {@code VentaRepository}). Esta clase existe para que la lógica de
 * cálculo y validación del pago viva en un solo lugar en vez de repetirse
 * en el service.
 */
public class Pago {

    private final MetodoPago metodoPago;
    private final double montoRecibido;
    private final double vuelto;

    /**
     * Crea un pago, validando que el monto recibido alcance para cubrir el total.
     *
     * @param metodoPago método utilizado para pagar
     * @param montoRecibido monto que entrega el cliente
     * @param total total a cobrar de la venta
     * @throws IllegalArgumentException si el monto recibido es menor que el total
     */
    public Pago(MetodoPago metodoPago, double montoRecibido, double total) {
        if (metodoPago == null) {
            throw new IllegalArgumentException("Debe indicar un método de pago");
        }
        if (montoRecibido < total) {
            throw new IllegalArgumentException(
                    "El monto recibido (" + montoRecibido + ") es menor que el total a pagar (" + total + ")");
        }
        this.metodoPago = metodoPago;
        this.montoRecibido = montoRecibido;
        this.vuelto = redondear(montoRecibido - total);
    }

    /**
     * Constructor privado usado solo para reconstruir un pago ya calculado
     * (al leer una venta desde el CSV), sin repetir la validación de negocio.
     */
    private Pago(MetodoPago metodoPago, double montoRecibido, double vuelto, boolean reconstruccion) {
        this.metodoPago = metodoPago;
        this.montoRecibido = montoRecibido;
        this.vuelto = vuelto;
    }

    /**
     * Reconstruye un pago ya validado (usado al leer una venta desde el CSV,
     * donde el vuelto ya viene calculado y no debe recalcularse).
     * recrea el objeto para el csv
     */
    public static Pago reconstruir(MetodoPago metodoPago, double montoRecibido, double vuelto) {
        return new Pago(metodoPago, montoRecibido, vuelto, true);
    }

    /** @return el método utilizado para pagar */
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    /** @return el monto que entregó el cliente */
    public double getMontoRecibido() {
        return montoRecibido;
    }

    /** @return el vuelto a entregar al cliente */
    public double getVuelto() {
        return vuelto;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return metodoPago + " | recibido: " + montoRecibido + " | vuelto: " + vuelto;
    }
}
