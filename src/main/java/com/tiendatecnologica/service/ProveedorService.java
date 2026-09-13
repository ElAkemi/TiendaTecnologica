package com.tiendatecnologica.service;

import com.tiendatecnologica.model.Proveedor;
import com.tiendatecnologica.repository.ProveedorRepository;

import java.util.List;

/**
 * Servicio encargado de gestionar las operaciones de los proveedores.
 *
 * Contiene las validaciones de negocio antes de utilizar el repositorio
 * para guardar, modificar, consultar o eliminar proveedores.
 */
public class ProveedorService {

    private final ProveedorRepository proveedorRepository =
            new ProveedorRepository();

    /**
     * Obtiene todos los proveedores registrados.
     *
     * @return lista de proveedores
     */
    public List<Proveedor> obtenerProveedores() {
        return proveedorRepository.obtenerTodos();
    }

    /**
     * Registra un nuevo proveedor.
     *
     * @param proveedor proveedor que se desea registrar
     */
    public void registrarProveedor(Proveedor proveedor) {
        validarDatos(proveedor);

        if (proveedorRepository.existe(proveedor.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe un proveedor con el ID " + proveedor.getId()
            );
        }

        proveedorRepository.guardar(proveedor);
    }

    /**
     * Modifica los datos de un proveedor existente.
     *
     * El ID no se modifica porque funciona como clave del proveedor.
     *
     * @param proveedor proveedor con los nuevos datos
     */
    public void actualizarProveedor(Proveedor proveedor) {
        validarDatos(proveedor);

        if (!proveedorRepository.existe(proveedor.getId())) {
            throw new IllegalArgumentException(
                    "No existe un proveedor con el ID " + proveedor.getId()
            );
        }

        proveedorRepository.actualizar(proveedor);
    }

    /**
     * Elimina un proveedor existente.
     *
     * @param id identificador del proveedor
     */
    public void eliminarProveedor(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un proveedor"
            );
        }

        if (!proveedorRepository.existe(id)) {
            throw new IllegalArgumentException(
                    "No existe un proveedor con el ID " + id
            );
        }

        proveedorRepository.eliminar(id);
    }

    /**
     * Valida los datos básicos de un proveedor.
     *
     * @param proveedor proveedor que se desea validar
     */
    private void validarDatos(Proveedor proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException(
                    "El proveedor no puede ser nulo"
            );
        }

        if (proveedor.getId() == null || proveedor.getId().isBlank()) {
            throw new IllegalArgumentException(
                    "El ID del proveedor es obligatorio"
            );
        }

        if (proveedor.getNombre() == null || proveedor.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del proveedor es obligatorio"
            );
        }
    }
}