package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.DetalleCompra;
import com.tiendatecnologica.model.OrdenCompra;
import com.tiendatecnologica.persistence.CsvReader;
import com.tiendatecnologica.persistence.CsvWriter;
import com.tiendatecnologica.util.Constantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de órdenes de compra.
 *
 * Hereda el CRUD de CsvRepository y define cómo se convierte
 * una OrdenCompra hacia y desde una fila de CSV.
 *
 * Los detalles de cada orden se almacenan en otro archivo CSV.
 */
public class OrdenCompraRepository extends CsvRepository<OrdenCompra> {

    private final CsvReader lector = new CsvReader();
    private final CsvWriter escritor = new CsvWriter();

    /**
     * Indica el archivo CSV donde se guardan las órdenes de compra.
     */
    @Override
    protected String rutaArchivo() {
        return Constantes.COMPRAS_CSV;
    }

    /**
     * Define los encabezados del archivo de órdenes de compra.
     */
    @Override
    protected String[] encabezado() {
        return new String[] {
                "idOrden",
                "idProveedor",
                "fecha",
                "estado"
        };
    }

    /**
     * Convierte una orden de compra en una fila de CSV.
     */
    @Override
    protected String[] mapearAFila(OrdenCompra orden) {
        return new String[] {
                orden.getIdOrden(),
                orden.getIdProveedor(),
                orden.getFecha(),
                orden.getEstado()
        };
    }

    /**
     * Convierte una fila del CSV en una OrdenCompra.
     *
     * También busca los detalles asociados a la orden.
     */
    @Override
    protected OrdenCompra mapearDesdeFila(String[] fila) {

        String idOrden = fila[0];

        List<DetalleCompra> detalles = cargarDetalles(idOrden);

        return new OrdenCompra(
                idOrden,
                fila[1],
                fila[2],
                fila[3],
                detalles
        );
    }

    /**
     * Obtiene el identificador de una orden de compra.
     */
    @Override
    protected String obtenerClave(OrdenCompra orden) {
        return orden.getIdOrden();
    }

    /**
     * Guarda una orden de compra y sus respectivos detalles.
     *
     * La orden se guarda utilizando el método heredado de CsvRepository.
     * Los detalles se guardan en su propio archivo CSV.
     */
    @Override
    public void guardar(OrdenCompra orden) {

        // Guarda la orden utilizando la lógica heredada.
        super.guardar(orden);

        // Verifica que la orden tenga detalles.
        if (orden.getDetalles() != null) {

            // Guarda cada detalle en el archivo correspondiente.
            for (DetalleCompra detalle : orden.getDetalles()) {

                escritor.agregarLinea(
                        Constantes.DETALLE_COMPRAS_CSV,
                        new String[] {
                                "idOrden",
                                "idProducto",
                                "cantidad",
                                "costoUnitario"
                        },
                        new String[] {
                                orden.getIdOrden(),
                                detalle.getIdProducto(),
                                String.valueOf(detalle.getCantidad()),
                                String.valueOf(detalle.getCostoUnitario())
                        }
                );
            }
        }
    }

    /**
     * Busca y carga los detalles pertenecientes a una orden.
     *
     * @param idOrdenBuscada identificador de la orden
     * @return lista de detalles asociados a la orden
     */
    private List<DetalleCompra> cargarDetalles(String idOrdenBuscada) {

        List<DetalleCompra> detalles = new ArrayList<>();

        // Lee todas las filas del archivo de detalles.
        for (String[] datos : lector.leer(Constantes.DETALLE_COMPRAS_CSV)) {

            // Verifica que la fila tenga los cuatro datos necesarios
            // y que pertenezca a la orden buscada.
            if (datos.length >= 4 && datos[0].equals(idOrdenBuscada)) {

                detalles.add(
                        new DetalleCompra(
                                datos[1],
                                Integer.parseInt(datos[2]),
                                Double.parseDouble(datos[3])
                        )
                );
            }
        }

        return detalles;
    }
}