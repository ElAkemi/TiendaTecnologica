package com.tiendatecnologica.service;

import com.tiendatecnologica.enums.EstadoVenta;
import com.tiendatecnologica.enums.MetodoPago;
import com.tiendatecnologica.model.DetalleVenta;
import com.tiendatecnologica.model.Pago;
import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.model.Venta;
import com.tiendatecnologica.repository.ClienteRepository;
import com.tiendatecnologica.repository.DetalleVentaRepository;
import com.tiendatecnologica.repository.VentaRepository;
import com.tiendatecnologica.util.Constantes;
import com.tiendatecnologica.util.GeneradorId;
import com.tiendatecnologica.util.Validador;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio del punto de venta: arma el carrito, calcula
 * subtotal/impuesto/total, valida el pago y confirma la venta.
 *
 * REGLA DE ORO DEL PROYECTO: esta clase nunca toca
 * {@code producto.setCantidadDisponible(...)} directamente. Todo descuento
 * o reintegro de stock pasa por {@link InventarioService}
 * ({@code registrarSalida} / {@code registrarEntrada}).
 */
public class VentaService {

    private final VentaRepository ventaRepository = new VentaRepository();
    private final DetalleVentaRepository detalleVentaRepository = new DetalleVentaRepository();
    private final ClienteRepository clienteRepository = new ClienteRepository();
    //se llaman tanto el productoservice como el invetario service para poder generar la venta
    private final ProductoService productoService = new ProductoService();
    private final InventarioService inventarioService = new InventarioService();

    /**
     * Prepara una línea de carrito para un producto: valida que exista,
     * que la cantidad sea válida y que haya stock suficiente, y "congela"
     * el precio de venta actual del producto. No descuenta inventario ni
     * persiste nada todavía — eso solo ocurre al confirmar la venta con
     * {@link #registrarVenta}.
     *
     * @param codigoProducto código del producto a agregar
     * @param cantidad cantidad deseada
     * @return una línea de carrito lista para mostrarse y, luego, facturarse
     * @throws IllegalArgumentException si el producto no existe o la cantidad no es válida
     * @throws IllegalStateException si no hay stock suficiente
     */
    public DetalleVenta crearItemCarrito(String codigoProducto, int cantidad) {
        Validador.validarTexto(codigoProducto, "Código de producto");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }

        Producto producto = productoService.buscarProducto(codigoProducto);
        if (producto == null) {
            throw new IllegalArgumentException("No existe el producto " + codigoProducto);
        }
        if (!inventarioService.hayStock(codigoProducto, cantidad)) {
            throw new IllegalStateException(
                    "Stock insuficiente de " + producto.getNombre()
                            + " (disponible: " + producto.getCantidadDisponible() + ")");
        }

        DetalleVenta item = new DetalleVenta(null, codigoProducto, cantidad, producto.getPrecioVenta());
        item.setNombreProducto(producto.getNombre());
        return item;
    }

    /**
     * Confirma una venta: valida stock, calcula subtotal/impuesto/total,
     * valida el pago, descuenta el inventario de cada producto vendido
     * (a través de {@link InventarioService#registrarSalida}) y persiste
     * la venta junto con sus líneas de detalle.
     *
     * @param idCliente id del cliente al que se le vende
     * @param items líneas del carrito (obtenidas con {@link #crearItemCarrito})
     * @param metodoPago método de pago utilizado
     * @param montoRecibido monto que entrega el cliente
     * @return la venta ya registrada, con su id y su vuelto calculado
     * @throws IllegalArgumentException si el cliente no existe, el carrito está vacío
     *         o alguna cantidad no es válida
     * @throws IllegalStateException si no hay stock suficiente de algún producto
     */
    public Venta registrarVenta(String idCliente, List<DetalleVenta> items,
                                MetodoPago metodoPago, double montoRecibido) {
        Validador.validarTexto(idCliente, "Cliente");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío");
        }
        if (!clienteRepository.existe(idCliente)) {
            throw new IllegalArgumentException("No existe el cliente " + idCliente);
        }

        // Se suma la cantidad por producto por si el mismo código aparece en
        // más de una línea del carrito, para validar el stock una sola vez
        // contra el total real que se va a descontar.
        Map<String, Integer> cantidadPorProducto = new LinkedHashMap<>();
        for (DetalleVenta item : items) {
            if (item.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
            }
            cantidadPorProducto.merge(item.getCodigoProducto(), item.getCantidad(), Integer::sum);
        }
        for (Map.Entry<String, Integer> entrada : cantidadPorProducto.entrySet()) {
            if (!inventarioService.hayStock(entrada.getKey(), entrada.getValue())) {
                throw new IllegalStateException("Stock insuficiente del producto " + entrada.getKey());
            }
        }

        double subtotal = 0;
        for (DetalleVenta item : items) {
            subtotal += item.getSubtotal();
        }
        subtotal = redondear(subtotal);
        double impuesto = redondear(subtotal * Constantes.IMPUESTO);
        double total = redondear(subtotal + impuesto);

        // Si el monto no alcanza, Pago lanza IllegalArgumentException y la
        // venta no llega a guardarse ni a tocar inventario.
        Pago pago = new Pago(metodoPago, montoRecibido, total);

        String idVenta = GeneradorId.generarIdVenta();
        Venta venta = new Venta(idVenta, LocalDateTime.now(), idCliente,
                subtotal, impuesto, total, pago, EstadoVenta.COMPLETADA);
        ventaRepository.guardar(venta);

        for (DetalleVenta item : items) {
            DetalleVenta detalle = new DetalleVenta(
                    idVenta, item.getCodigoProducto(), item.getCantidad(), item.getPrecioUnitario());
            detalleVentaRepository.guardar(detalle);
            inventarioService.registrarSalida(item.getCodigoProducto(), item.getCantidad(), "Venta " + idVenta);
        }

        return venta;
    }

    /**
     * Anula una venta ya confirmada: reintegra al inventario todo lo
     * vendido en ella (a través de {@link InventarioService#registrarEntrada})
     * y marca la venta como ANULADA. El registro no se borra, queda en el
     * historial con su nuevo estado.
     *
     * @param idVenta id de la venta a anular
     * @throws IllegalArgumentException si la venta no existe
     * @throws IllegalStateException si la venta ya estaba anulada
     */
    public void anularVenta(String idVenta) {
        Venta venta = ventaRepository.buscarPorClave(idVenta);
        if (venta == null) {
            throw new IllegalArgumentException("No existe la venta " + idVenta);
        }
        if (!venta.estaCompletada()) {
            throw new IllegalStateException("La venta " + idVenta + " ya está anulada");
        }

        for (DetalleVenta detalle : detalleVentaRepository.obtenerPorVenta(idVenta)) {
            inventarioService.registrarEntrada(
                    detalle.getCodigoProducto(), detalle.getCantidad(), "Anulación venta " + idVenta);
        }

        venta.setEstado(EstadoVenta.ANULADA);
        ventaRepository.actualizar(venta);
    }

    /**
     * Busca una venta por su id.
     *
     * @param idVenta id a buscar
     * @return la venta encontrada, o null si no existe
     */
    public Venta buscarVenta(String idVenta) {
        return ventaRepository.buscarPorClave(idVenta);
    }

    /**
     * @return todas las ventas registradas
     */
    public List<Venta> listarVentas() {
        return ventaRepository.obtenerTodos();
    }

    /**
     * Obtiene las líneas de detalle de una venta, con el nombre del
     * producto ya resuelto para mostrarlo en pantalla.
     *
     * @param idVenta id de la venta a consultar
     * @return lista de líneas de esa venta
     */
    public List<DetalleVenta> obtenerDetalle(String idVenta) {
        List<DetalleVenta> detalles = detalleVentaRepository.obtenerPorVenta(idVenta);
        for (DetalleVenta detalle : detalles) {
            Producto producto = productoService.buscarProducto(detalle.getCodigoProducto());
            if (producto != null) {
                detalle.setNombreProducto(producto.getNombre());
            }
        }
        return detalles;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
