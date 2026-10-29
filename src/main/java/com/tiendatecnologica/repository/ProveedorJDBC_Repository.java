package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.Proveedor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProveedorJDBC_Repository extends JdbcRepository<Proveedor> {

    // Nombre de la tabla en MySQL.
    @Override
    protected String tabla() {
        return "proveedor";
    }

    // Columna que identifica de forma única a cada proveedor.
    @Override
    protected String columnaClave() {
        return "id";
    }

    // Palabra para los mensajes de error: "No se pudo guardar el proveedor".
    @Override
    protected String entidad() {
        return "proveedor";
    }

    // Las 5 columnas de la tabla, en orden, y 5 signos ? (uno por columna).
    @Override
    protected String sqlInsertar() {
        return "INSERT INTO proveedor (id, nombre, contacto, telefono, correo) "
                + "VALUES (?, ?, ?, ?, ?)";
    }

    // El UPDATE no modifica id: la clave solo se usa en el WHERE
    // para saber qué fila cambiar.
    @Override
    protected String sqlActualizar() {
        return "UPDATE proveedor SET nombre = ?, contacto = ?, telefono = ?, "
                + "correo = ? "
                + "WHERE id = ?";
    }

    // Valores del INSERT, en el mismo orden que las 5 columnas.
    @Override // Se usa Object[] porque mezcla tipos distintos (texto, decimales, enteros).
    protected Object[] parametrosInsertar(Proveedor p) {
        return new Object[] {
                p.getId(), p.getNombre(), p.getContacto(),
                p.getTelefono(), p.getCorreo()
        };
    }

    // Valores del UPDATE; el id va al final porque es el ? del WHERE.
    @Override // Se usa Object[] porque mezcla tipos distintos (texto, decimales, enteros).
    protected Object[] parametrosActualizar(Proveedor p) {
        return new Object[] {
                p.getNombre(), p.getContacto(), p.getTelefono(),
                p.getCorreo(), p.getId()//va al final por como se declara el UPDATE , en este el id va al final
        };
    }

    // Recibe una fila de MySQL y reconstruye el objeto Proveedor.
    @Override
    protected Proveedor mapear(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getString("id"),
                rs.getString("nombre"),
                rs.getString("contacto"),
                rs.getString("telefono"),
                rs.getString("correo"));
    }
}