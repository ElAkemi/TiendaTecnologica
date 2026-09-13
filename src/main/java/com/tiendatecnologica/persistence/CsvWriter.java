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
 * Escritura de archivos CSV.
 *
 * escribir(): reescribe el archivo completo (se usa para actualizar/eliminar).
 * Escribe primero a un archivo temporal y luego reemplaza el original,
 * para no dejar el CSV corrupto si el programa se cierra a mitad de la escritura.
 *
 * agregarLinea(): agrega una sola línea al final (se usa para inserciones simples).
 */
public class CsvWriter {

    public void escribir(String ruta, List<String[]> datos) {
        Path destino = Path.of(ruta);
        Path temporal = Path.of(ruta + ".tmp");

        try {
            crearCarpetaSiNoExiste(destino);

            String contenido = datos.stream()
                    .map(fila -> String.join(",", fila))
                    .collect(Collectors.joining(System.lineSeparator()));

            Files.writeString(temporal, contenido + System.lineSeparator(), StandardCharsets.UTF_8);
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo el archivo: " + ruta, e);
        }
    }

    public void agregarLinea(String ruta, String[] encabezado, String[] datos) {
        Path destino = Path.of(ruta);
        try {
            crearCarpetaSiNoExiste(destino);
            StringBuilder contenido = new StringBuilder();
            if (!Files.exists(destino) || Files.size(destino) == 0) {
                contenido.append(String.join(",", encabezado))
                        .append(System.lineSeparator());
            }
            contenido.append(String.join(",", datos))
                    .append(System.lineSeparator());
            Files.writeString(destino, contenido, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Error agregando línea al archivo: " + ruta, e);
        }
    }

    private void crearCarpetaSiNoExiste(Path archivo) throws IOException {
        Path carpeta = archivo.getParent();
        if (carpeta != null && !Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }
    }
}
