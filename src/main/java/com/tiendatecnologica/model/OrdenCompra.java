package com.tiendatecnologica.model;

import java.util.List;

/**
 * Representa una orden de compra realizada a un proveedor.
 *
 * Contiene la información general de la orden y la lista de
 * productos incluidos en ella.
 */
public class OrdenCompra {

    /** Identificador único de la orden de compra. */
    private String idOrden;

    /** Identificador del proveedor al que se realiza la compra. */
    private String idProveedor;

    /** Fecha en la que se registra la orden. */
    private String fecha;

    /** Estado actual de la orden: Pendiente, Recibida o Cancelada. */
    private String estado;

    /** Lista de productos incluidos en la orden de compra. */
    private List<DetalleCompra> detalles;

    /**
     * Crea una nueva orden de compra.
     *
     * @param idOrden identificador de la orden
     * @param idProveedor identificador del proveedor
     * @param fecha fecha de la orden
     * @param estado estado inicial de la orden
     * @param detalles lista de detalles de la compra
     */
    public OrdenCompra(String idOrden, String idProveedor, String fecha,
                       String estado, List<DetalleCompra> detalles) {
        this.idOrden = idOrden;
        this.idProveedor = idProveedor;
        this.fecha = fecha;
        this.estado = estado;
        this.detalles = detalles;
    }

    /**
     * Obtiene el identificador de la orden.
     *
     * @return identificador de la orden
     */
    public String getIdOrden() {
        return idOrden;
    }

    /**
     * Modifica el identificador de la orden.
     *
     * @param idOrden nuevo identificador
     */
    public void setIdOrden(String idOrden) {
        this.idOrden = idOrden;
    }

    /**
     * Obtiene el identificador del proveedor.
     *
     * @return identificador del proveedor
     */
    public String getIdProveedor() {
        return idProveedor;
    }

    /**
     * Modifica el identificador del proveedor.
     *
     * @param idProveedor nuevo identificador del proveedor
     */
    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * Obtiene la fecha de la orden.
     *
     * @return fecha de la orden
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Modifica la fecha de la orden.
     *
     * @param fecha nueva fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el estado actual de la orden.
     *
     * @return estado de la orden
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Modifica el estado de la orden.
     *
     * @param estado nuevo estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene los detalles de la orden.
     *
     * @return lista de detalles de la compra
     */
    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    /**
     * Modifica los detalles de la orden.
     *
     * @param detalles nueva lista de detalles
     */
    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }

    /**
     * Calcula el costo total de la orden de compra.
     *
     * Suma el subtotal de cada detalle incluido en la orden.
     *
     * @return total de la orden
     */
    public double calcularTotal() {
        double total = 0.0;

        if (detalles != null) {
            for (DetalleCompra detalle : detalles) {
                total += detalle.calcularSubtotal();
            }
        }

        return total;
    }
}