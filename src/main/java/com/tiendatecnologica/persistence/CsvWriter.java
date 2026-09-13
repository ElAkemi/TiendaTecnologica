package com.tiendatecnologica.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase encargada de escribir información en archivos CSV.
 *
 * escribir(): reescribe el archivo completo. Se utiliza principalmente
 * para actualizar o eliminar registros.
 *
 * La escritura se realiza primero en un archivo temporal y posteriormente
 * se reemplaza el archivo original, reduciendo el riesgo de dejar el CSV
 * incompleto si ocurre un problema durante la escritura.
 *
 * agregarLinea(): agrega una nueva línea al final del archivo.
 * Se utiliza para inserciones simples.
 */
public class CsvWriter {

    /**
     * Reescribe completamente un archivo CSV con los datos proporcionados.
     *
     * Primero se genera el contenido en un archivo temporal y posteriormente
     * se reemplaza el archivo original.
     *
     * @param ruta ruta del archivo CSV que se desea escribir
     * @param datos filas que se escribirán en el archivo
     * @throws RuntimeException si ocurre un error durante la escritura
     */
    public void escribir(String ruta, List<String[]> datos) {
        Path destino = Path.of(ruta);
        Path temporal = Path.of(ruta + ".tmp");

        try {
            // Crea la carpeta del archivo si todavía no existe.
            crearCarpetaSiNoExiste(destino);

            /*
             * Convierte cada fila en una línea de texto separando
             * sus valores mediante comas.
             */
            String contenido = datos.stream()
                    .map(fila -> String.join(",", fila))
                    .collect(Collectors.joining(System.lineSeparator()));

            /*
             * Escribe primero el contenido en el archivo temporal.
             * Esto evita modificar directamente el archivo original.
             */
            Files.writeString(
                    temporal,
                    contenido + System.lineSeparator(),
                    StandardCharsets.UTF_8
            );

            /*
             * Reemplaza el archivo original por el archivo temporal
             * una vez que la escritura se completó correctamente.
             */
            Files.move(
                    temporal,
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error escribiendo el archivo: " + ruta,
                    e
            );
        }
    }

    /**
     * Agrega una nueva línea al final de un archivo CSV.
     *
     * Si el archivo no existe o está vacío, primero se escribe
     * el encabezado proporcionado.
     *
     * @param ruta ruta del archivo CSV
     * @param encabezado encabezado que se utilizará si el archivo está vacío
     * @param datos datos de la nueva fila que se agregará
     * @throws RuntimeException si ocurre un error durante la escritura
     */
    public void agregarLinea(
            String ruta,
            String[] encabezado,
            String[] datos
    ) {
        Path destino = Path.of(ruta);

        try {
            // Crea la carpeta del archivo si todavía no existe.
            crearCarpetaSiNoExiste(destino);

            StringBuilder contenido = new StringBuilder();

            /*
             * Si el archivo no existe o está vacío, se agrega primero
             * la fila correspondiente al encabezado.
             */
            if (!Files.exists(destino) || Files.size(destino) == 0) {
                contenido.append(String.join(",", encabezado))
                        .append(System.lineSeparator());
            }

            // Agrega los datos de la nueva fila.
            contenido.append(String.join(",", datos))
                    .append(System.lineSeparator());

            /*
             * Crea el archivo si no existe y agrega el contenido
             * al final sin eliminar los registros anteriores.
             */
            Files.writeString(
                    destino,
                    contenido,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error agregando línea al archivo: " + ruta,
                    e
            );
        }
    }

    /**
     * Crea la carpeta que contiene el archivo si todavía no existe.
     *
     * Si el archivo se encuentra directamente en la carpeta del proyecto,
     * {@code getParent()} puede devolver null y no se crea ninguna carpeta.
     *
     * @param archivo ruta del archivo cuya carpeta se desea comprobar
     * @throws IOException si no es posible crear la carpeta
     */
    private void crearCarpetaSiNoExiste(Path archivo) throws IOException {
        Path carpeta = archivo.getParent();

        if (carpeta != null && !Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
    }
}