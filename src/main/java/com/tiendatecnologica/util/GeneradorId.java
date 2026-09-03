package com.tiendatecnologica.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Genera IDs correlativos (V-00001, C-00001, etc.) contando las líneas
 * de datos ya existentes en el CSV correspondiente.
 *
 * NOTA: esta estrategia es suficiente para el alcance del curso
 * (una sola instancia de la app, un solo usuario a la vez). No es segura
 * ante escrituras concurrentes.
 */
public final class GeneradorId {

    private GeneradorId() {
    }

    public static String generarIdVenta() {
        return generar("V-", Constantes.VENTAS_CSV);
    }

    public static String generarIdCompra() {
        return generar("C-", Constantes.COMPRAS_CSV);
    }

    public static String generarIdMovimiento() {
        return generar("MOV-", Constantes.MOVIMIENTOS_CSV);
    }

    public static String generarIdGarantia() {
        return generar("GAR-", Constantes.GARANTIAS_CSV);
    }

    public static String generarIdDevolucion() {
        return generar("DEV-", Constantes.DEVOLUCIONES_CSV);
    }

    public static String generarIdPago() {
        return generar("PAGO-", null);
    }

    public static String generarIdCliente() {
        return generar("CLI-", Constantes.CLIENTES_CSV);
    }

    private static String generar(String prefijo, String rutaCsv) {
        int siguiente = 1;
        if (rutaCsv != null) {
            try {
                Path path = Path.of(rutaCsv);
                if (Files.exists(path)) {
                    long lineas = Files.lines(path).count();
                    // -1 por el encabezado; si el archivo solo tiene encabezado, siguiente = 1
                    siguiente = (int) Math.max(1, lineas);
                }
            } catch (IOException e) {
                throw new RuntimeException("No se pudo leer " + rutaCsv + " para generar el ID", e);
            }
        }
        return prefijo + String.format("%05d", siguiente);
    }
}
