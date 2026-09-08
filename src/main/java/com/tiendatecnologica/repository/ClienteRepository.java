package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.Cliente;
import com.tiendatecnologica.util.Constantes;
//CLASE ENCARGADA DE RECUPERAR Y GUARDAR CLIENTES EN EL CSV
/**
 * Repositorio de clientes. Único responsable de leer/escribir clientes.csv.
 * Hereda todo el CRUD de {@link CsvRepository} y solo define cómo se
 * convierte un Cliente hacia/desde una fila de CSV.
 */

public class ClienteRepository extends CsvRepository<Cliente> {
    /** {@inheritDoc} */
    @Override
    protected String rutaArchivo() {
        return Constantes.CLIENTES_CSV;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] encabezado() {
        return new String[] {"id", "cedula", "nombre", "telefono", "email"};
    }

    /** {@inheritDoc} */
    @Override
    protected String[] mapearAFila(Cliente c) {
        return new String[] {c.getId(), c.getCedula(), c.getNombre(), c.getTelefono(), c.getEmail()};
    }

    /** {@inheritDoc} */
    @Override
    protected Cliente mapearDesdeFila(String[] fila) {
        return new Cliente(fila[0], fila[1], fila[2], fila[3], fila[4]);
    }

    /** {@inheritDoc} */
    @Override
    protected String obtenerClave(Cliente c) {
        return c.getId();
    }

    /**
     * Busca un cliente por su cédula (además de por id, que ya cubre
     * {@code buscarPorClave}).
     *
     * @param cedula cédula a buscar
     * @return el cliente encontrado, o null si no existe
     */
    public Cliente buscarPorCedula(String cedula) {
        for (Cliente c : obtenerTodos()) {
            if (c.getCedula().equals(cedula)) {
                return c;
            }
        }
        return null;
    }
}
