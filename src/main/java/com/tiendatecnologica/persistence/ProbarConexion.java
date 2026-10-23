package com.tiendatecnologica.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Programa pequeño para verificar que la conexión a MySQL funciona.
 *
 * Se ejecuta con clic derecho sobre la clase en IntelliJ > Run.
 * Si todo está bien, imprime la cantidad de productos de la base
 * (con el seed.sql cargado deben ser 5).
 */
public class ProbarConexion {

    /**
     * Abre una conexión y cuenta las filas de la tabla producto.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        String sql = "SELECT COUNT(*) FROM producto";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                System.out.println("Conexión correcta. Productos en la BD: " + rs.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión o consulta: " + e.getMessage());
        }
    }
}