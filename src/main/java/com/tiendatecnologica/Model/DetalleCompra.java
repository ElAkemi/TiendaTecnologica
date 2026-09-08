package com.tiendatecnologica.model;

public class DetalleCompra {
    private String idProducto;
    private int cantidad;
    private double costoUnitario;

    public DetalleCompra(String idProducto, int cantidad, double costoUnitario) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public double calcularSubtotal() {
        return this.cantidad * this.costoUnitario;
    }
}
