package com.tiendatecnologica.model;

/**
 * Fila de resumen para el reporte de "productos más vendidos": un producto
 * y la cantidad total de unidades que se le han vendido. No representa una
 * entidad persistida; existe solo para mostrarse en pantalla, la calcula
 * {@code ReporteService} a partir del historial de ventas.
 */
public class ProductoVendido {

    private final String codigoProducto;
    private final String nombreProducto;
    private final int cantidadVendida;

    /**
     * Crea una fila de resumen de ventas por producto.
     *
     * @param codigoProducto código del producto
     * @param nombreProducto nombre del producto, para mostrar en pantalla
     * @param cantidadVendida cantidad total de unidades vendidas de ese producto
     */
    public ProductoVendido(String codigoProducto, String nombreProducto, int cantidadVendida) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
    }

    /** @return el código del producto */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /** @return el nombre del producto */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /** @return la cantidad total de unidades vendidas */
    public int getCantidadVendida() {
        return cantidadVendida;
    }
}
