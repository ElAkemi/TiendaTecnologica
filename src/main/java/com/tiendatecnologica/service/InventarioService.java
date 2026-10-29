package com.tiendatecnologica.service;

import com.tiendatecnologica.model.AjusteInventario;
import com.tiendatecnologica.model.EntradaInventario;
import com.tiendatecnologica.model.MovimientoInventario;
import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.model.SalidaInventario;
import com.tiendatecnologica.persistence.Transaccion;
import com.tiendatecnologica.repository.InventarioRepository;
import com.tiendatecnologica.util.GeneradorId;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


public class InventarioService {

    private final InventarioRepository inventarioRepository = new InventarioRepository();


    public void registrarEntrada(String codigoProducto, int cantidad, String motivo) {
        Transaccion.ejecutar(con -> {
            registrarEntrada(con, codigoProducto, cantidad, motivo);
            return null;
        });
    }
    
    public void registrarSalida(String codigoProducto, int cantidad, String motivo) {
        Transaccion.ejecutar(con -> {
            registrarSalida(con, codigoProducto, cantidad, motivo);
            return null;
        });
    }

    public void ajustarInventario(String codigoProducto, int nuevaCantidad, String motivo) {
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La nueva cantidad no puede ser negativa");
        }
        Transaccion.ejecutar(con -> {
            MovimientoInventario movimiento = new AjusteInventario(
                    GeneradorId.generarIdMovimiento(con), codigoProducto, nuevaCantidad,
                    LocalDateTime.now(), motivo);
            aplicarYGuardar(con, codigoProducto, movimiento);
            return null;
        });
    }

    public void registrarEntrada(Connection con, String codigoProducto, int cantidad, String motivo)
            throws SQLException {
        validarCantidad(cantidad);
        MovimientoInventario movimiento = new EntradaInventario(
                GeneradorId.generarIdMovimiento(con), codigoProducto, cantidad,
                LocalDateTime.now(), motivo);
        aplicarYGuardar(con, codigoProducto, movimiento);
    }

    public void registrarSalida(Connection con, String codigoProducto, int cantidad, String motivo)
            throws SQLException {
        validarCantidad(cantidad);
        MovimientoInventario movimiento = new SalidaInventario(
                GeneradorId.generarIdMovimiento(con), codigoProducto, cantidad,
                LocalDateTime.now(), motivo);
        aplicarYGuardar(con, codigoProducto, movimiento);
    }

    public int obtenerExistencia(String codigoProducto) {
        return inventarioRepository.obtenerStock(codigoProducto);
    }

    public List<MovimientoInventario> obtenerMovimientos() {
        return inventarioRepository.obtenerTodos();
    }

    public List<MovimientoInventario> obtenerMovimientosProducto(String codigoProducto) {
        return inventarioRepository.obtenerMovimientosPorProducto(codigoProducto);
    }

    public List<Producto> obtenerProductosBajoStock() {
        return new ProductoService().obtenerProductosBajoStock();
    }

    public List<Producto> obtenerProductosAgotados() {
        return new ProductoService().obtenerProductosAgotados();
    }

    public boolean hayStock(String codigoProducto, int cantidad) {
        return inventarioRepository.obtenerStock(codigoProducto) >= cantidad;
    }

    private void aplicarYGuardar(Connection con, String codigoProducto, MovimientoInventario movimiento)
            throws SQLException {
        int actual = inventarioRepository.obtenerStockBloqueando(con, codigoProducto);
        int nueva = movimiento.aplicar(actual);

        inventarioRepository.actualizarStock(con, codigoProducto, nueva);
        inventarioRepository.guardar(con, movimiento);
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
    }
}
