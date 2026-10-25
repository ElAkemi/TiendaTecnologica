package com.tiendatecnologica.persistence;

import java.sql.SQLException;

/**
 * Error de acceso a datos con un mensaje ya pensado para el usuario.
 *
 * Los repositorios JDBC atrapan las {@link SQLException} (que son checked y
 * llenas de detalles técnicos) y las convierten en esta excepción. Así:
 * <ul>
 *   <li>Los services y controllers no necesitan conocer JDBC.</li>
 *   <li>Los controllers pueden mostrar {@link #getMessage()} en una alerta
 *       y la aplicación sigue funcionando en lugar de cerrarse.</li>
 * </ul>
 * El detalle técnico original queda en la causa ({@link #getCause()}) y se
 * imprime en la consola para depuración.
 */
public class RepositorioException extends RuntimeException {

    // Códigos de error de MySQL que se traducen a mensajes claros.
    private static final int ERR_ACCESO_DENEGADO = 1045;
    private static final int ERR_BD_DESCONOCIDA = 1049;
    private static final int ERR_TABLA_NO_EXISTE = 1146;
    private static final int ERR_DUPLICADO = 1062;
    private static final int ERR_DATO_MUY_LARGO = 1406;
    private static final int ERR_FK_HIJO = 1452;
    private static final int ERR_FK_PADRE = 1451;
    private static final int ERR_CHECK = 3819;

    public RepositorioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public RepositorioException(String mensaje) {
        super(mensaje);
    }

    /**
     * Traduce una {@link SQLException} a un mensaje entendible.
     *
     * @param e      error original de JDBC
     * @param accion lo que se intentaba hacer, en infinitivo (ej. "guardar el producto")
     * @return la excepción lista para lanzar
     */
    public static RepositorioException desde(SQLException e, String accion) {
        System.err.println("[BD] Error al " + accion + ": "
                + e.getErrorCode() + " / " + e.getSQLState() + " / " + e.getMessage());

        String detalle;
        switch (e.getErrorCode()) {
            case ERR_DUPLICADO ->
                    detalle = "Ya existe un registro con esos datos (valor duplicado en un campo único).";
            case ERR_FK_PADRE ->
                    detalle = "No se puede eliminar porque tiene registros relacionados "
                            + "(ventas, compras, movimientos, garantías o devoluciones).";
            case ERR_FK_HIJO ->
                    detalle = "Hace referencia a un registro que no existe.";
            case ERR_CHECK ->
                    detalle = "Algún valor no es válido (no puede ser negativo).";
            case ERR_DATO_MUY_LARGO ->
                    detalle = "Algún texto es demasiado largo para el campo.";
            case ERR_ACCESO_DENEGADO ->
                    detalle = "Usuario o contraseña de la base de datos incorrectos "
                            + "(revise db.properties).";
            case ERR_BD_DESCONOCIDA ->
                    detalle = "La base de datos no existe. Ejecute schema.sql primero.";
            case ERR_TABLA_NO_EXISTE ->
                    detalle = "Faltan tablas en la base de datos. Ejecute schema.sql.";
            default -> {
                String estado = e.getSQLState();
                if (e.getMessage() != null && e.getMessage().contains("db.properties")) {
                    // Error de configuración propio (ConexionBD): ya trae el mensaje claro.
                    detalle = e.getMessage();
                } else if (estado != null && estado.startsWith("08")) {
                    detalle = "No se pudo conectar con la base de datos. "
                            + "Verifique que MySQL esté encendido.";
                } else {
                    detalle = "Error de base de datos: " + e.getMessage();
                }
            }
        }
        return new RepositorioException("No se pudo " + accion + ". " + detalle, e);
    }
}
