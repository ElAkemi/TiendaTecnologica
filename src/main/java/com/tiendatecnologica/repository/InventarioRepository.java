package com.tiendatecnologica.repository;

import com.tiendatecnologica.enums.TipoMovimiento;
import com.tiendatecnologica.model.AjusteInventario;
import com.tiendatecnologica.model.EntradaInventario;
import com.tiendatecnologica.model.MovimientoInventario;
import com.tiendatecnologica.model.SalidaInventario;
import com.tiendatecnologica.persistence.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository implements RegistroRepository<MovimientoInventario> {

    private static final String COLUMNAS =
            "id, codigo_producto, tipo, cantidad, cantidad_anterior, cantidad_nueva, fecha, motivo";

    @Override
    public List<MovimientoInventario> obtenerTodos() {
        String sql = "SELECT " + COLUMNAS + " FROM movimiento_inventario ORDER BY fecha, id";
        List<MovimientoInventario> movimientos = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                movimientos.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw error("leer los movimientos", e);
        }
        return movimientos;
    }

    @Override
    public void guardar(MovimientoInventario movimiento) {
        try (Connection con = ConexionBD.getConexion()) {
            guardar(con, movimiento);
        } catch (SQLException e) {
            throw error("guardar el movimiento", e);
        }
    }

    public List<MovimientoInventario> obtenerMovimientosPorProducto(String codigoProducto) {
        String sql = "SELECT " + COLUMNAS
                + " FROM movimiento_inventario WHERE codigo_producto = ? ORDER BY fecha, id";
        List<MovimientoInventario> movimientos = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw error("leer los movimientos del producto " + codigoProducto, e);
        }
        return movimientos;
    }

    public int obtenerStock(String codigoProducto) {
        String sql = "SELECT cantidad_disponible FROM producto WHERE codigo = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("No existe el producto " + codigoProducto);
                }
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw error("consultar la existencia de " + codigoProducto, e);
        }
    }

    public int obtenerStockBloqueando(Connection con, String codigoProducto) throws SQLException {
        String sql = "SELECT cantidad_disponible FROM producto WHERE codigo = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("No existe el producto " + codigoProducto);
                }
                return rs.getInt(1);
            }
        }
    }

    public void actualizarStock(Connection con, String codigoProducto, int nuevaCantidad)
            throws SQLException {
        String sql = "UPDATE producto SET cantidad_disponible = ? WHERE codigo = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setString(2, codigoProducto);
            ps.executeUpdate();
        }
    }

    public void guardar(Connection con, MovimientoInventario m) throws SQLException {
        String sql = "INSERT INTO movimiento_inventario (" + COLUMNAS + ") VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getId());
            ps.setString(2, m.getCodigoProducto());
            ps.setString(3, m.getTipo().name());
            ps.setInt(4, m.getCantidad());
            ps.setInt(5, m.getCantidadAnterior());
            ps.setInt(6, m.getCantidadNueva());
            ps.setTimestamp(7, Timestamp.valueOf(m.getFecha()));
            ps.setString(8, m.getMotivo());
            ps.executeUpdate();
        }
    }

    private MovimientoInventario mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String codigo = rs.getString("codigo_producto");
        TipoMovimiento tipo = TipoMovimiento.valueOf(rs.getString("tipo"));
        int cantidad = rs.getInt("cantidad");
        int anterior = rs.getInt("cantidad_anterior");
        int nueva = rs.getInt("cantidad_nueva");
        LocalDateTime fecha = rs.getTimestamp("fecha").toLocalDateTime();
        String motivo = rs.getString("motivo");

        MovimientoInventario movimiento = switch (tipo) {
            case ENTRADA -> new EntradaInventario(id, codigo, cantidad, fecha, motivo);
            case SALIDA -> new SalidaInventario(id, codigo, cantidad, fecha, motivo);
            case AJUSTE -> new AjusteInventario(id, codigo, nueva, fecha, motivo);
        };

        movimiento.fijarCantidad(cantidad);
        movimiento.fijarHistorial(anterior, nueva);
        return movimiento;
    }

    private IllegalStateException error(String accion, SQLException e) {
        return new IllegalStateException(
                "Error de base de datos al " + accion + ": " + e.getMessage(), e);
    }
}
