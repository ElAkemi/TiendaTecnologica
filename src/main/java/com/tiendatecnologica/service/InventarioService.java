package com.tiendatecnologica.service;

import com.tiendatecnologica.model.AjusteInventario;
import com.tiendatecnologica.model.EntradaInventario;
import com.tiendatecnologica.model.MovimientoInventario;
import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.model.SalidaInventario;
import com.tiendatecnologica.repository.InventarioRepository;
import com.tiendatecnologica.repository.ProductoRepository;
import com.tiendatecnologica.util.GeneradorId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REGLA DE ORO DEL PROYECTO: toda modificación de la cantidad disponible
 * de un Producto pasa por aquí. Nadie más (VentaService, CompraService,
 * etc.) debe llamar producto.setCantidadDisponible(...) directamente.
 */
public class InventarioService {

    private final ProductoRepository productoRepository = new ProductoRepository();
    private final InventarioRepository inventarioRepository = new InventarioRepository();

    /**
     * Registra una entrada de inventario (ej. recepción de una compra).
     *
     * @param codigoProducto código del producto que recibe inventario
     * @param cantidad cantidad que ingresa (debe ser mayor que 0)
     * @param motivo razón de la entrada
     * @throws IllegalArgumentException si la cantidad no es válida o el producto no existe
     */
    public void registrarEntrada(String codigoProducto, int cantidad, String motivo) {
        validarCantidad(cantidad);
        MovimientoInventario movimiento = new EntradaInventario(
                GeneradorId.generarIdMovimiento(), codigoProducto, cantidad, LocalDateTime.now(), motivo);
        aplicarYGuardar(codigoProducto, movimiento);
    }

    /**
     * Registra una salida de inventario (ej. una venta).
     *
     * @param codigoProducto código del producto que pierde inventario
     * @param cantidad cantidad que sale (debe ser mayor que 0)
     * @param motivo razón de la salida
     * @throws IllegalArgumentException si la cantidad no es válida o el producto no existe
     * @throws IllegalStateException si no hay suficiente stock disponible
     */
    public void registrarSalida(String codigoProducto, int cantidad, String motivo) {
        validarCantidad(cantidad);
        MovimientoInventario movimiento = new SalidaInventario(
                GeneradorId.generarIdMovimiento(), codigoProducto, cantidad, LocalDateTime.now(), motivo);
        aplicarYGuardar(codigoProducto, movimiento);
    }

    /**
     * Ajusta el inventario de un producto a un valor exacto (ej. tras un conteo físico).
     *
     * @param codigoProducto código del producto a ajustar
     * @param nuevaCantidad cantidad final que debe quedar en inventario (no negativa)
     * @param motivo razón del ajuste
     * @throws IllegalArgumentException si la cantidad es negativa o el producto no existe
     */
    public void ajustarInventario(String codigoProducto, int nuevaCantidad, String motivo) {
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La nueva cantidad no puede ser negativa");
        }
        MovimientoInventario movimiento = new AjusteInventario(
                GeneradorId.generarIdMovimiento(), codigoProducto, nuevaCantidad, LocalDateTime.now(), motivo);
        aplicarYGuardar(codigoProducto, movimiento);
    }

    /**
     * Obtiene la cantidad actualmente disponible de un producto.
     *
     * @param codigoProducto código del producto a consultar
     * @return cantidad disponible
     * @throws IllegalArgumentException si el producto no existe
     */
    public int obtenerExistencia(String codigoProducto) {
        return obtenerProductoObligatorio(codigoProducto).getCantidadDisponible();
    }

    /**
     * @return el historial completo de movimientos de inventario
     */
    public List<MovimientoInventario> obtenerMovimientos() {
        return inventarioRepository.obtenerTodos();
    }

    /**
     * Obtiene el historial de movimientos de un producto específico.
     *
     * @param codigoProducto código del producto a filtrar
     * @return lista de movimientos de ese producto
     */
    public List<MovimientoInventario> obtenerMovimientosProducto(String codigoProducto) {
        return inventarioRepository.obtenerMovimientosPorProducto(codigoProducto);
    }

    /**
     * @return productos cuyo stock está en o por debajo del mínimo, pero no agotados
     */
    public List<Producto> obtenerProductosBajoStock() {
        return new ProductoService().obtenerProductosBajoStock();
    }

    /**
     * @return productos con cantidad disponible igual a 0
     */
    public List<Producto> obtenerProductosAgotados() {
        return new ProductoService().obtenerProductosAgotados();
    }

    /**
     * Verifica si hay suficiente stock de un producto para cubrir una cantidad.
     *
     * @param codigoProducto código del producto a verificar
     * @param cantidad cantidad requerida
     * @return true si el stock disponible es mayor o igual a la cantidad requerida
     */
    public boolean hayStock(String codigoProducto, int cantidad) {
        return obtenerProductoObligatorio(codigoProducto).getCantidadDisponible() >= cantidad;
    }

    /**
     * Aplica un movimiento sobre el producto correspondiente (polimorfismo:
     * cada subtipo de MovimientoInventario sabe calcular su propia nueva
     * cantidad), actualiza el producto y deja el movimiento en el historial.
     *
     * @param codigoProducto código del producto afectado
     * @param movimiento movimiento ya construido, pendiente de aplicar
     */
    private void aplicarYGuardar(String codigoProducto, MovimientoInventario movimiento) {
        Producto producto = obtenerProductoObligatorio(codigoProducto);
        int nueva = movimiento.aplicar(producto.getCantidadDisponible());

        producto.setCantidadDisponible(nueva);
        productoRepository.actualizar(producto);
        inventarioRepository.guardar(movimiento);
    }

    /**
     * Valida que una cantidad de movimiento sea mayor que 0.
     *
     * @param cantidad cantidad a validar
     * @throws IllegalArgumentException si la cantidad no es mayor que 0
     */
    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
    }

    /**
     * Busca un producto por código y lanza una excepción si no existe.
     *
     * @param codigoProducto código a buscar
     * @return el producto encontrado
     * @throws IllegalArgumentException si no existe un producto con ese código
     */
    private Producto obtenerProductoObligatorio(String codigoProducto) {
        Producto producto = productoRepository.buscarPorClave(codigoProducto);
        if (producto == null) {
            throw new IllegalArgumentException("No existe el producto " + codigoProducto);
        }
        return producto;
    }
}
