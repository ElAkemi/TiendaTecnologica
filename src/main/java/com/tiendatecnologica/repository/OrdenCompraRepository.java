package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.DetalleCompra;
import com.tiendatecnologica.model.OrdenCompra;
import com.tiendatecnologica.persistence.ConexionBD;
import com.tiendatecnologica.persistence.Transaccion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraRepository implements Repository<OrdenCompra> {

    @Override
    public List<OrdenCompra> obtenerTodos() {
        String sql = "SELECT id_orden, id_proveedor, fecha, estado FROM orden_compra "
                + "ORDER BY fecha, id_orden";
        List<OrdenCompra> ordenes = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ordenes.add(mapear(con, rs));
            }
        } catch (SQLException e) {
            throw error("leer las órdenes de compra", e);
        }
        return ordenes;
    }

    @Override
    public OrdenCompra buscarPorClave(String idOrden) {
        try (Connection con = ConexionBD.getConexion()) {
            return buscarPorClave(con, idOrden);
        } catch (SQLException e) {
            throw error("buscar la orden " + idOrden, e);
        }
    }

    @Override
    public void guardar(OrdenCompra orden) {
        Transaccion.ejecutar(con -> {
            guardar(con, orden);
            return null;
        });
    }

    @Override
    public void actualizar(OrdenCompra orden) {
        try (Connection con = ConexionBD.getConexion()) {
            actualizarEstado(con, orden.getIdOrden(), orden.getEstado());
        } catch (SQLException e) {
            throw error("actualizar la orden " + orden.getIdOrden(), e);
        }
    }

    @Override
    public void eliminar(String idOrden) {
        String sql = "DELETE FROM orden_compra WHERE id_orden = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idOrden);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw error("eliminar la orden " + idOrden, e);
        }
    }

    @Override
    public boolean existe(String idOrden) {
        return buscarPorClave(idOrden) != null;
    }

    public OrdenCompra buscarPorClave(Connection con, String idOrden) throws SQLException {
        String sql = "SELECT id_orden, id_proveedor, fecha, estado FROM orden_compra "
                + "WHERE id_orden = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(con, rs) : null;
            }
        }
    }

    public void guardar(Connection con, OrdenCompra orden) throws SQLException {
        String sqlOrden = "INSERT INTO orden_compra (id_orden, id_proveedor, fecha, estado) "
                + "VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sqlOrden)) {
            ps.setString(1, orden.getIdOrden());
            ps.setString(2, orden.getIdProveedor());
            ps.setDate(3, convertirFecha(orden.getFecha()));
            ps.setString(4, estadoABd(orden.getEstado()));
            ps.executeUpdate();
        }

        if (orden.getDetalles() == null) {
            return;
        }
        String sqlDetalle = "INSERT INTO detalle_compra (id_orden, codigo_producto, cantidad, costo_unitario) "
                + "VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
            for (DetalleCompra d : orden.getDetalles()) {
                ps.setString(1, orden.getIdOrden());
                ps.setString(2, d.getIdProducto());
                ps.setInt(3, d.getCantidad());
                ps.setBigDecimal(4, BigDecimal.valueOf(d.getCostoUnitario()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void actualizarEstado(Connection con, String idOrden, String estado) throws SQLException {
        String sql = "UPDATE orden_compra SET estado = ? WHERE id_orden = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estadoABd(estado));
            ps.setString(2, idOrden);
            ps.executeUpdate();
        }
    }

    private OrdenCompra mapear(Connection con, ResultSet rs) throws SQLException {
        String idOrden = rs.getString("id_orden");
        return new OrdenCompra(
                idOrden,
                rs.getString("id_proveedor"),
                rs.getDate("fecha").toLocalDate().toString(),
                estadoDesdeBd(rs.getString("estado")),
                cargarDetalles(con, idOrden));
    }

    private List<DetalleCompra> cargarDetalles(Connection con, String idOrden) throws SQLException {
        String sql = "SELECT codigo_producto, cantidad, costo_unitario FROM detalle_compra "
                + "WHERE id_orden = ? ORDER BY id";
        List<DetalleCompra> detalles = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetalleCompra(
                            rs.getString("codigo_producto"),
                            rs.getInt("cantidad"),
                            rs.getDouble("costo_unitario")));
                }
            }
        }
        return detalles;
    }

    private Date convertirFecha(String fecha) {
        try {
            return Date.valueOf(LocalDate.parse(fecha.trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato AAAA-MM-DD (ej. 2026-10-28)");
        }
    }

    private String estadoABd(String estado) {
        return estado.trim().toUpperCase();
    }

    private String estadoDesdeBd(String estado) {
        return estado.substring(0, 1) + estado.substring(1).toLowerCase();
    }

    private IllegalStateException error(String accion, SQLException e) {
        return new IllegalStateException(
                "Error de base de datos al " + accion + ": " + e.getMessage(), e);
    }
}
