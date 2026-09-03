package com.tiendatecnologica.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lectura de archivos CSV. La primera línea se asume como encabezado
 * y se omite del resultado.
 */
public class CsvReader {

    public List<String[]> leer(String ruta) {
        return leerDesdeArchivo(new File(ruta));
    }

    public List<String[]> leerDesdeArchivo(File archivo) {
        List<String[]> filas = new ArrayList<>();

        if (!archivo.exists()) {
            return filas;
        }

        try {
            List<String> lineas = Files.readAllLines(Path.of(archivo.getPath()), StandardCharsets.UTF_8);

            for (int i = 1; i < lineas.size(); i++) { // i = 1 -> se salta el encabezado
                String linea = lineas.get(i);
                if (linea.isBlank()) {
                    continue;
                }
                filas.add(linea.split(",", -1));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo el archivo: " + archivo.getPath(), e);
        }

        return filas;
    }
}
