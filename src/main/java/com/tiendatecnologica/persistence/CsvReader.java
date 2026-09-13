package com.tiendatecnologica.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de leer archivos CSV.
 *
 * La primera línea de cada archivo se considera el encabezado
 * y no se incluye dentro de las filas devueltas.
 */
public class CsvReader {

    /**
     * Lee un archivo CSV a partir de su ruta.
     *
     * @param ruta ruta del archivo CSV que se desea leer
     * @return lista de filas del archivo, donde cada fila se representa
     *         como un arreglo de cadenas
     */
    public List<String[]> leer(String ruta) {
        return leerDesdeArchivo(new File(ruta));
    }

    /**
     * Lee un archivo CSV y convierte cada fila en un arreglo de cadenas.
     *
     * Si el archivo no existe, se devuelve una lista vacía.
     * La primera línea se omite porque corresponde al encabezado.
     *
     * @param archivo archivo CSV que se desea leer
     * @return lista de filas del archivo
     * @throws RuntimeException si ocurre un error al leer el archivo
     */
    public List<String[]> leerDesdeArchivo(File archivo) {
        /** Lista donde se almacenan las filas leídas del archivo. */
        List<String[]> filas = new ArrayList<>();

        /*
         * Si el archivo no existe, no se considera un error.
         * Simplemente se devuelve una lista vacía.
         */
        if (!archivo.exists()) {
            return filas;
        }

        try {
            /*
             * Lee todas las líneas del archivo utilizando UTF-8
             * para mantener correctamente los caracteres especiales.
             */
            List<String> lineas = Files.readAllLines(
                    Path.of(archivo.getPath()),
                    StandardCharsets.UTF_8
            );

            /*
             * Se comienza desde la posición 1 porque la posición 0
             * corresponde al encabezado del archivo CSV.
             */
            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i);

                /*
                 * Ignora líneas vacías para evitar crear registros
                 * innecesarios en la lista de resultados.
                 */
                if (linea.isBlank()) {
                    continue;
                }

                /*
                 * Divide la línea utilizando la coma como separador.
                 * El parámetro -1 permite conservar campos vacíos
                 * al final de la línea.
                 */
                filas.add(linea.split(",", -1));
            }

        } catch (IOException e) {
            /*
             * Se convierte la excepción de entrada/salida en una
             * RuntimeException para informar al resto del sistema
             * que no fue posible leer el archivo.
             */
            throw new RuntimeException(
                    "Error leyendo el archivo: " + archivo.getPath(),
                    e
            );
        }

        return filas;
    }
}