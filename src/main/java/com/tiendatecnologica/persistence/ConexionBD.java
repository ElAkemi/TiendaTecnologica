package com.tiendatecnologica.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase encargada de abrir conexiones con la base de datos MySQL.
 *
 * Los datos de conexión (URL, usuario y contraseña) se leen del archivo
 * {@code db.properties}, ubicado en {@code src/main/resources}. Ese archivo
 * no se sube a Git; cada integrante copia {@code db.properties.example},
 * lo renombra a {@code db.properties} y completa sus propios datos.
 *
 * Cada llamada a {@link #getConexion()} devuelve una conexión NUEVA.
 * Quien la pida debe cerrarla (lo más simple es usar try-with-resources),
 * así varios hilos (por ejemplo los Task de JavaFX) no comparten una
 * misma conexión.
 */
public final class ConexionBD {

    /** Nombre del archivo de configuración dentro de resources. */
    private static final String ARCHIVO_CONFIG = "db.properties";

    /** Datos de conexión leídos una sola vez al cargar la clase. */
    private static final Properties CONFIG = cargarConfiguracion();

    private ConexionBD() {
        // Clase utilitaria, no se instancia
    }

    /**
     * Abre una nueva conexión con la base de datos.
     *
     * Debe cerrarse después de usarla:
     * <pre>
     * try (Connection con = ConexionBD.getConexion()) {
     *     // usar la conexión
     * }
     * </pre>
     *
     * @return una conexión abierta a MySQL
     * @throws SQLException si no se puede conectar (servidor apagado,
     *                      usuario o contraseña incorrectos, etc.)
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password")
        );
    }

    /**
     * Comprueba si es posible conectarse a la base de datos.
     *
     * Sirve para validar la configuración al iniciar la aplicación
     * sin que un error cierre el programa.
     *
     * @return true si la conexión se abrió correctamente, false en caso contrario
     */
    public static boolean probarConexion() {
        try (Connection con = getConexion()) {
            return con.isValid(2);
        } catch (SQLException e) {
            System.err.println("No se pudo conectar a la base de datos: "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * Lee el archivo db.properties desde el classpath.
     *
     * @return las propiedades con los datos de conexión
     * @throws IllegalStateException si el archivo no existe o no se puede leer
     */
    private static Properties cargarConfiguracion() {
        Properties props = new Properties();

        try (InputStream entrada = ConexionBD.class
                .getClassLoader().getResourceAsStream(ARCHIVO_CONFIG)) {

            if (entrada == null) {
                throw new IllegalStateException(
                        "No se encontró " + ARCHIVO_CONFIG + " en src/main/resources. "
                                + "Copie db.properties.example como db.properties "
                                + "y complete sus datos.");
            }
            props.load(entrada);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error leyendo " + ARCHIVO_CONFIG, e);
        }
        return props;
    }
}