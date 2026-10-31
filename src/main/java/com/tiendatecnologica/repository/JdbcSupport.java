package com.tiendatecnologica.repository;

import com.tiendatecnologica.persistence.ConexionBD;
//clase para traducir los errores de mysql de codigo a palabras para el usuario
import com.tiendatecnologica.persistence.RepositorioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Herramientas JDBC compartidas por todos los repositorios que usan MySQL:
 * abrir y cerrar la conexión, ejecutar con {@link PreparedStatement} (nunca se
 * concatenan valores en el SQL, así no hay inyección SQL) y traducir cualquier
 * {@link SQLException} a una {@link RepositorioException}.
 *
 * {@link JdbcRepository} la usa para el CRUD genérico; las tablas que no
 * encajan en ese molde (como la asociación N:M) la usan directamente.
 */
public abstract class JdbcSupport {

    /** Convierte la fila actual de un ResultSet en un objeto. */
    @FunctionalInterface
    protected interface Mapeador<R> {
        R mapear(ResultSet rs) throws SQLException;
    }

    // ------------------------------------------------------------------
    // Utilidades para las subclases
    // ------------------------------------------------------------------

    /**
     * Ejecuta un SELECT y convierte cada fila con el mapeador.
     *
     * @param sql     consulta con '?' para los parámetros
     * @param accion  qué se intentaba hacer, para el mensaje de error
     * @param mapeador convierte cada fila en un objeto
     * @param params  valores de los '?'
     */

    //basicamente hace las consultas select para no repetir la logica en todo el programa
    protected <R> List<R> consultar(String sql, String accion,
                                    Mapeador<R> mapeador, Object... params) {
        List<R> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignar(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapeador.mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw RepositorioException.desde(e, accion);
        }
        return resultado;
    }

    /**
     * Ejecuta un INSERT, UPDATE o DELETE.
     *
     * @return cantidad de filas afectadas
     */

    //sirve para las demas operaciones de sql , INSERT, UPDATE y DELETE , estas se hacen con la logica de ejecutar
    //y retorna las filas afectadas
    protected int ejecutar(String sql, String accion, Object... params) {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignar(ps, params);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw RepositorioException.desde(e, accion);
        }
    }

    private static void asignar(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
