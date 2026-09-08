package com.tiendatecnologica.model;

import java.util.List;

public class OrdenCompra {
    private String idOrden;
    private String idProveedor;
    private String fecha;
    private String estado;
    private List<DetalleCompra> detalles;

    public OrdenCompra(String idOrden, String idProveedor, String fecha, String estado, List<DetalleCompra> detalles) {
        this.idOrden = idOrden;
        this.idProveedor = idProveedor;
        this.fecha = fecha;
        this.estado = estado;
        this.detalles = detalles;
    }

    public String getIdOrden() { return idOrden; }
    public void setIdOrden(String idOrden) { this.idOrden = idOrden; }

    public String getIdProveedor() { return idProveedor; }
    public void setIdProveedor(String idProveedor) { this.idProveedor = idProveedor; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<DetalleCompra> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCompra> detalles) { this.detalles = detalles; }

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
