package com.tiendatecnologica.service;

import com.tiendatecnologica.model.DetalleVenta;
import com.tiendatecnologica.model.OrdenCompra;
import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.model.ProductoVendido;
import com.tiendatecnologica.model.Venta;
import com.tiendatecnologica.repository.DetalleVentaRepository;
import com.tiendatecnologica.repositorio.OrdenCompraRepositorio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agrega información de otros módulos (ventas, inventario, compras) para
 * generar los reportes que pide el enunciado. Es una capa de solo lectura:
 * no persiste nada, solo combina y calcula a partir de los repositorios y
 * servicios existentes.
 */
public class ReporteService {

    private final VentaService ventaService = new VentaService();
    private final ProductoService productoService = new ProductoService();
    private final DetalleVentaRepository detalleVentaRepository = new DetalleVentaRepository();
    private final OrdenCompraRepositorio ordenCompraRepositorio = new OrdenCompraRepositorio();

    /**
     * @return todas las ventas registradas (completadas y anuladas), para mostrarlas en el reporte
     */
    public List<Venta> obtenerVentas() {
        return ventaService.listarVentas();
    }

    /**
     * Calcula, para cada producto, la cantidad total de unidades vendidas
     * en ventas COMPLETADAS (las anuladas no suman), ordenado de mayor a
     * menor cantidad vendida.
     *
     * @return lista de productos con su cantidad vendida, ordenada descendentemente
     */
    public List<ProductoVendido> obtenerProductosMasVendidos() {
        Map<String, Integer> cantidadPorProducto = new LinkedHashMap<>();

        for (Venta venta : ventaService.listarVentas()) {
            if (!venta.estaCompletada()) {
                continue;
            }
            for (DetalleVenta detalle : detalleVentaRepository.obtenerPorVenta(venta.getId())) {
                cantidadPorProducto.merge(detalle.getCodigoProducto(), detalle.getCantidad(), Integer::sum);
            }
        }

        List<ProductoVendido> resultado = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : cantidadPorProducto.entrySet()) {
            Producto producto = productoService.buscarProducto(entrada.getKey());
            String nombre = producto == null ? entrada.getKey() : producto.getNombre();
            resultado.add(new ProductoVendido(entrada.getKey(), nombre, entrada.getValue()));
        }
        resultado.sort(Comparator.comparingInt(ProductoVendido::getCantidadVendida).reversed());
        return resultado;
    }

    /**
     * @return productos cuyo stock está en o por debajo del mínimo (incluye agotados)
     */
    public List<Producto> obtenerProductosBajoInventario() {
        List<Producto> resultado = new ArrayList<>(productoService.obtenerProductosBajoStock());
        resultado.addAll(productoService.obtenerProductosAgotados());
        return resultado;
    }

    /**
     * @return todas las órdenes de compra realizadas a proveedores
     */
    public List<OrdenCompra> obtenerCompras() {
        return ordenCompraRepositorio.listar();
    }

    /**
     * Calcula la ganancia estimada de todas las ventas COMPLETADAS, como la
     * diferencia entre el precio al que se vendió cada línea (congelado en
     * el detalle de venta) y el precio de compra ACTUAL del producto. Es una
     * estimación: si el precio de compra de un producto cambió después de
     * venderlo, no se puede saber cuál era el costo exacto en ese momento.
     *
     * @return la ganancia estimada total, redondeada a 2 decimales
     */
    public double calcularGananciasEstimadas() {
        double ganancia = 0;
        for (Venta venta : ventaService.listarVentas()) {
            if (!venta.estaCompletada()) {
                continue;
            }
            for (DetalleVenta detalle : detalleVentaRepository.obtenerPorVenta(venta.getId())) {
                Producto producto = productoService.buscarProducto(detalle.getCodigoProducto());
                if (producto != null) {
                    ganancia += (detalle.getPrecioUnitario() - producto.getPrecioCompra()) * detalle.getCantidad();
                }
            }
        }
        return Math.round(ganancia * 100.0) / 100.0;
    }
}
