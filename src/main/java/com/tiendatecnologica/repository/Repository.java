package com.tiendatecnologica.repository;

import java.util.List;

/**
 * Contrato común para acceder a entidades que se pueden crear, leer,
 * actualizar y eliminar (CRUD completo). Cualquier repositorio que
 * maneje entidades editables (ej. Producto, Proveedor, Cliente) debe
 * implementar esta interfaz, aunque esas entidades no compartan una
 * jerarquía de herencia entre sí.
 *
 * @param <T> tipo de la entidad que administra el repositorio
 */
public interface Repository<T> {

    /**
     * Obtiene todos los registros almacenados.
     *
     * @return lista con todas las entidades encontradas; vacía si no hay ninguna
     */
    List<T> obtenerTodos();

    /**
     * Busca una entidad por su clave única (código, id, etc.).
     *
     * @param clave identificador único de la entidad
     * @return la entidad encontrada, o null si no existe
     */
    T buscarPorClave(String clave);

    /**
     * Guarda una nueva entidad.
     *
     * @param entidad entidad a guardar
     */
    void guardar(T entidad);

    /**
     * Actualiza una entidad existente, identificada por su clave.
     *
     * @param entidad entidad con los datos actualizados
     */
    void actualizar(T entidad);

    /**
     * Elimina una entidad por su clave.
     *
     * @param clave identificador único de la entidad a eliminar
     */
    void eliminar(String clave);

    /**
     * Indica si existe una entidad con la clave indicada.
     *
     * @param clave identificador único a buscar
     * @return true si existe, false en caso contrario
     */
    boolean existe(String clave);
}
