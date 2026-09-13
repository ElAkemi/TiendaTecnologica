package com.tiendatecnologica.repository;

import com.tiendatecnologica.persistence.CsvReader;
import com.tiendatecnologica.persistence.CsvWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base genérica para repositorios que persisten entidades editables en un
 * archivo CSV. Implementa el CRUD completo definido en {@link Repository}
 * usando el patrón "método plantilla": las subclases solo indican cómo
 * convertir su entidad concreta hacia/desde una fila de CSV, y esta clase
 * se encarga de leer, escribir, buscar, actualizar y eliminar.
 *
 * @param <T> tipo de la entidad que administra el repositorio
 */
public abstract class CsvRepository<T> implements Repository<T> {

    protected final CsvReader lector = new CsvReader();
    protected final CsvWriter escritor = new CsvWriter();

    /** {@inheritDoc} */
    @Override
    public List<T> obtenerTodos() {
        List<T> resultado = new ArrayList<>();
        for (String[] fila : lector.leer(rutaArchivo())) {
            resultado.add(mapearDesdeFila(fila));
        }
        return resultado;
    }

    /** {@inheritDoc} */
    @Override
    public T buscarPorClave(String clave) {
        for (T entidad : obtenerTodos()) {
            if (obtenerClave(entidad).equals(clave)) {
                return entidad;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void guardar(T entidad) {
        escritor.agregarLinea(
                rutaArchivo(),
                encabezado(),
                mapearAFila(entidad)
        );
    }

    /** {@inheritDoc} */
    @Override
    public void actualizar(T entidad) {
        List<String[]> filas = new ArrayList<>();
        filas.add(encabezado());

        for (T actual : obtenerTodos()) {
            if (obtenerClave(actual).equals(obtenerClave(entidad))) {
                filas.add(mapearAFila(entidad));
            } else {
                filas.add(mapearAFila(actual));
            }
        }
        escritor.escribir(rutaArchivo(), filas);
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(String clave) {
        List<String[]> filas = new ArrayList<>();
        filas.add(encabezado());

        for (T actual : obtenerTodos()) {
            if (!obtenerClave(actual).equals(clave)) {
                filas.add(mapearAFila(actual));
            }
        }
        escritor.escribir(rutaArchivo(), filas);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existe(String clave) {
        return buscarPorClave(clave) != null;
    }

    /**
     * @return la ruta del archivo CSV donde vive esta entidad
     */
    protected abstract String rutaArchivo();

    /**
     * @return la fila de encabezado del CSV (nombres de columna, en orden)
     */
    protected abstract String[] encabezado();

    /**
     * Convierte una entidad a la fila de CSV que la representa.
     *
     * @param entidad entidad a convertir
     * @return arreglo de strings con los valores de cada columna, en orden
     */
    protected abstract String[] mapearAFila(T entidad);

    /**
     * Reconstruye una entidad a partir de una fila leída del CSV.
     *
     * @param fila arreglo de strings con los valores de cada columna
     * @return la entidad reconstruida
     */
    protected abstract T mapearDesdeFila(String[] fila);

    /**
     * Obtiene la clave única de una entidad, usada para comparar en
     * actualizar/eliminar/buscar (ej. el código de un producto).
     *
     * @param entidad entidad de la cual extraer la clave
     * @return el valor de la clave única
     */
    protected abstract String obtenerClave(T entidad);
}
