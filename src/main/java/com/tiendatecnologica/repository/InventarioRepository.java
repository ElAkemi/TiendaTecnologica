package com.tiendatecnologica.repository;

import com.tiendatecnologica.enums.TipoMovimiento;
import com.tiendatecnologica.model.AjusteInventario;
import com.tiendatecnologica.model.EntradaInventario;
import com.tiendatecnologica.model.MovimientoInventario;
import com.tiendatecnologica.model.SalidaInventario;
import com.tiendatecnologica.persistence.CsvReader;
import com.tiendatecnologica.persistence.CsvWriter;
import com.tiendatecnologica.util.Constantes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de movimientos de inventario.
 *
 * Es un historial: los movimientos no se actualizan ni se eliminan.
 * Solamente se agregan nuevos movimientos y se consultan los existentes.
 *
 * Por esta razón implementa {@link RegistroRepository} y no
 * {@link Repository}.
 */
public class InventarioRepository
        implements RegistroRepository<MovimientoInventario> {

    private final CsvReader lector = new CsvReader();
    private final CsvWriter escritor = new CsvWriter();

    /** Encabezados utilizados en movimientos.csv. */
    private static final String[] ENCABEZADO = {
            "id",
            "codigoProducto",
            "tipo",
            "cantidad",
            "cantidadAnterior",
            "cantidadNueva",
            "fecha",
            "motivo"
    };

    /** {@inheritDoc} */
    @Override
    public List<MovimientoInventario> obtenerTodos() {

        List<MovimientoInventario> movimientos =
                new ArrayList<>();

        for (String[] fila :
                lector.leer(Constantes.MOVIMIENTOS_CSV)) {

            movimientos.add(mapearDesdeFila(fila));
        }

        return movimientos;
    }

    /** {@inheritDoc} */
    @Override
    public void guardar(MovimientoInventario movimiento) {

        escritor.agregarLinea(
                Constantes.MOVIMIENTOS_CSV,
                ENCABEZADO,
                movimiento.toCsv()
        );
    }

    /**
     * Obtiene los movimientos asociados a un producto específico.
     *
     * @param codigoProducto código del producto a filtrar
     * @return lista de movimientos del producto
     */
    public List<MovimientoInventario> obtenerMovimientosPorProducto(
            String codigoProducto) {

        List<MovimientoInventario> resultado =
                new ArrayList<>();

        for (MovimientoInventario movimiento : obtenerTodos()) {

            if (movimiento.getCodigoProducto()
                    .equals(codigoProducto)) {

                resultado.add(movimiento);
            }
        }

        return resultado;
    }

    /**
     * Reconstruye un movimiento concreto a partir de una fila
     * del archivo movimientos.csv.
     *
     * @param fila arreglo de strings con los valores de la fila
     * @return movimiento reconstruido
     */
    private MovimientoInventario mapearDesdeFila(String[] fila) {

        String id = fila[0];
        String codigoProducto = fila[1];

        TipoMovimiento tipo =
                TipoMovimiento.valueOf(fila[2]);

        int cantidad =
                Integer.parseInt(fila[3]);

        int cantidadAnterior =
                Integer.parseInt(fila[4]);

        int cantidadNueva =
                Integer.parseInt(fila[5]);

        LocalDateTime fecha =
                LocalDateTime.parse(fila[6]);

        String motivo = fila[7];

        MovimientoInventario movimiento =
                switch (tipo) {

                    case ENTRADA -> new EntradaInventario(
                            id,
                            codigoProducto,
                            cantidad,
                            fecha,
                            motivo
                    );

                    case SALIDA -> new SalidaInventario(
                            id,
                            codigoProducto,
                            cantidad,
                            fecha,
                            motivo
                    );

                    case AJUSTE -> new AjusteInventario(
                            id,
                            codigoProducto,
                            cantidadNueva,
                            fecha,
                            motivo
                    );
                };

        /*
         * Restaura la cantidad que estaba almacenada en el CSV.
         * Esto es especialmente importante para los ajustes,
         * ya que su constructor comienza con cantidad 0.
         */
        movimiento.fijarCantidad(cantidad);

        /*
         * Restaura el historial del movimiento sin volver
         * a ejecutar la lógica del movimiento.
         */
        movimiento.fijarHistorial(
                cantidadAnterior,
                cantidadNueva
        );

        return movimiento;
    }
}