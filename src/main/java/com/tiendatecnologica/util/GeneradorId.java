package com.tiendatecnologica.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase utilitaria encargada de generar identificadores correlativos
 * para las diferentes entidades y registros del sistema.
 *
 * Los identificadores utilizan un prefijo según el tipo de registro
 * y un número de cinco dígitos, por ejemplo: V-00001, C-00001 o MOV-00001.
 *
 * La numeración se obtiene contando las líneas existentes en el archivo
 * CSV correspondiente.
 *
 * NOTA: esta estrategia es suficiente para el alcance del curso
 * (una sola instancia de la aplicación y un solo usuario a la vez).
 * No es segura ante escrituras concurrentes.
 */
public final class GeneradorId {

    /**
     * Constructor privado para evitar que la clase sea instanciada.
     *
     * Esta clase solamente contiene métodos estáticos y funciona
     * como una utilidad para todo el sistema.
     */
    private GeneradorId() {
    }

    /**
     * Genera un identificador para una venta.
     *
     * @return identificador con formato V-00001
     */
    public static String generarIdVenta() {
        return generar("V-", Constantes.VENTAS_CSV);
    }

    /**
     * Genera un identificador para una orden de compra.
     *
     * @return identificador con formato C-00001
     */
    public static String generarIdCompra() {
        return generar("C-", Constantes.COMPRAS_CSV);
    }

    /**
     * Genera un identificador para un movimiento de inventario.
     *
     * @return identificador con formato MOV-00001
     */
    public static String generarIdMovimiento() {
        return generar("MOV-", Constantes.MOVIMIENTOS_CSV);
    }

    /**
     * Genera un identificador para una garantía.
     *
     * @return identificador con formato GAR-00001
     */
    public static String generarIdGarantia() {
        return generar("GAR-", Constantes.GARANTIAS_CSV);
    }

    /**
     * Genera un identificador para una devolución.
     *
     * @return identificador con formato DEV-00001
     */
    public static String generarIdDevolucion() {
        return generar("DEV-", Constantes.DEVOLUCIONES_CSV);
    }

    /**
     * Genera un identificador para un pago.
     *
     * Como los pagos no utilizan un archivo CSV independiente
     * para calcular la numeración, se utiliza únicamente el prefijo.
     *
     * @return identificador con formato PAGO-00001
     */
    public static String generarIdPago() {
        return generar("PAGO-", null);
    }

    /**
     * Genera un identificador para un cliente.
     *
     * @return identificador con formato CLI-00001
     */
    public static String generarIdCliente() {
        return generar("CLI-", Constantes.CLIENTES_CSV);
    }

    /**
     * Genera un identificador utilizando un prefijo y el archivo CSV
     * correspondiente.
     *
     * Si el archivo existe, se cuentan sus líneas para determinar
     * el siguiente número disponible. La primera línea corresponde
     * al encabezado, por lo que el número de líneas permite obtener
     * directamente el siguiente identificador.
     *
     * Si el archivo no existe, la numeración comienza en 1.
     *
     * @param prefijo prefijo que identifica el tipo de registro
     * @param rutaCsv ruta del archivo CSV utilizado para calcular
     *                la numeración; puede ser null
     * @return identificador generado con cinco dígitos
     * @throws RuntimeException si ocurre un error al leer el archivo CSV
     */
    private static String generar(String prefijo, String rutaCsv) {
        int siguiente = 1;

        /*
         * Si existe un archivo CSV asociado, se utiliza la cantidad
         * de líneas existentes para determinar el siguiente número.
         */
        if (rutaCsv != null) {
            try {
                Path path = Path.of(rutaCsv);

                if (Files.exists(path)) {
                    long lineas = Files.lines(path).count();

                    /*
                     * La primera línea corresponde al encabezado.
                     * Math.max(1, lineas) garantiza que la numeración
                     * comience como mínimo en 1.
                     */
                    siguiente = (int) Math.max(1, lineas);
                }

            } catch (IOException e) {
                throw new RuntimeException(
                        "No se pudo leer " + rutaCsv
                                + " para generar el ID",
                        e
                );
            }
        }

        /*
         * %05d asegura que el número tenga siempre cinco dígitos.
         * Por ejemplo: 1 -> 00001, 25 -> 00025.
         */
        return prefijo + String.format("%05d", siguiente);
    }
}