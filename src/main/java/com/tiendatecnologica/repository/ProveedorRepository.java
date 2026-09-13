package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.Proveedor;
import com.tiendatecnologica.util.Constantes;

/**
 * Repositorio de proveedores.
 *
 * Hereda el CRUD de CsvRepository y define cómo se convierte
 * un Proveedor hacia y desde una fila de CSV.
 */
public class ProveedorRepository extends CsvRepository<Proveedor> {

    /**
     * Indica el archivo CSV donde se guardan los proveedores.
     */
    @Override
    protected String rutaArchivo() {
        return Constantes.PROVEEDORES_CSV;
    }

    /**
     * Define los encabezados del archivo de proveedores.
     */
    @Override
    protected String[] encabezado() {
        return new String[] {
                "id",
                "nombre",
                "contacto",
                "telefono",
                "correo"
        };
    }

    /**
     * Convierte un proveedor en una fila de CSV.
     */
    @Override
    protected String[] mapearAFila(Proveedor p) {
        return new String[] {
                p.getId(),
                p.getNombre(),
                p.getContacto(),
                p.getTelefono(),
                p.getCorreo()
        };
    }

    /**
     * Convierte una fila del CSV en un objeto Proveedor.
     */
    @Override
    protected Proveedor mapearDesdeFila(String[] fila) {
        return new Proveedor(
                fila[0],
                fila[1],
                fila[2],
                fila[3],
                fila[4]
        );
    }

    /**
     * Obtiene el identificador único del proveedor.
     */
    @Override
    protected String obtenerClave(Proveedor p) {
        return p.getId();
    }
}