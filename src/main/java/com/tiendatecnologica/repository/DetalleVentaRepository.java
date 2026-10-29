package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.DetalleVenta;
import com.tiendatecnologica.persistence.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaRepository implements RegistroRepository<DetalleVenta> {

    private static final String COLUMNAS = "id_venta, codigo_producto, cantidad, precio_unitario";

    @Override
    public List<DetalleVenta> obtenerTodos() {
        String sql = "SELECT " + COLUMNAS + " FROM detalle_venta ORDER BY id";
        List<DetalleVenta> detalles = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                detalles.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw error("leer los detalles de venta", e);
        }
        return detalles;
    }

    @Override
    public void guardar(DetalleVenta detalle) {
        try (Connection con = ConexionBD.getConexion()) {
            guardar(con, detalle);
        } catch (SQLException e) {
            throw error("guardar el detalle de venta", e);
        }
    }

    public void guardar(Connection con, DetalleVenta d) throws SQLException {
        String sql = "INSERT INTO detalle_venta (" + COLUMNAS + ") VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getIdVenta());
            ps.setString(2, d.getCodigoProducto());
            ps.setInt(3, d.getCantidad());
            ps.setBigDecimal(4, BigDecimal.valueOf(d.getPrecioUnitario()));
            ps.executeUpdate();
        }
    }

    public List<DetalleVenta> obtenerPorVenta(String idVenta) {
        String sql = "SELECT " + COLUMNAS + " FROM detalle_venta WHERE id_venta = ? ORDER BY id";
        List<DetalleVenta> detalles = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw error("leer el detalle de la venta " + idVenta, e);
        }
        return detalles;
    }

    private DetalleVenta mapear(ResultSet rs) throws SQLException {
        return new DetalleVenta(
                rs.getString("id_venta"),
                rs.getString("codigo_producto"),
                rs.getInt("cantidad"),
                rs.getDouble("precio_unitario"));
    }

    private IllegalStateException error(String accion, SQLException e) {
        return new IllegalStateException(
                "Error de base de datos al " + accion + ": " + e.getMessage(), e);
    }
}
