package com.tiendatecnologica.model;

/**
 * Representa un detalle dentro de una orden de compra.
 *
 * Contiene la información de un producto comprado, la cantidad
 * solicitada y su costo unitario.
 */
public class DetalleCompra {

    /** Identificador del producto incluido en la compra. */
    private String idProducto;

    /** Cantidad de unidades del producto. */
    private int cantidad;

    /** Costo de una unidad del producto. */
    private double costoUnitario;

    /**
     * Crea un nuevo detalle de compra.
     *
     * @param idProducto identificador del producto
     * @param cantidad cantidad de unidades compradas
     * @param costoUnitario costo de una unidad del producto
     */
    public DetalleCompra(String idProducto, int cantidad, double costoUnitario) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return identificador del producto
     */
    public String getIdProducto() {
        return idProducto;
    }

    /**
     * Modifica el identificador del producto.
     *
     * @param idProducto nuevo identificador del producto
     */
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Obtiene la cantidad de unidades.
     *
     * @return cantidad de unidades
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Modifica la cantidad de unidades.
     *
     * @param cantidad nueva cantidad
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el costo unitario del producto.
     *
     * @return costo unitario
     */
    public double getCostoUnitario() {
        return costoUnitario;
    }

    /**
     * Modifica el costo unitario del producto.
     *
     * @param costoUnitario nuevo costo unitario
     */
    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    /**
     * Calcula el subtotal del detalle de compra.
     *
     * El subtotal se obtiene multiplicando la cantidad
     * de unidades por el costo unitario.
     *
     * @return subtotal del detalle
     */
    public double calcularSubtotal() {
        return this.cantidad * this.costoUnitario;
    }
}