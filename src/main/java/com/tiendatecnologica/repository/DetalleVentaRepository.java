package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.DetalleVenta;
import com.tiendatecnologica.persistence.CsvReader;
import com.tiendatecnologica.persistence.CsvWriter;
import com.tiendatecnologica.util.Constantes;

import java.util.ArrayList;
import java.util.List;
//igual, solo crea y lee csv
/**
 * Repositorio de líneas de venta (detalle_ventas.csv). Igual que
 * {@code InventarioRepository}, es un historial: una vez facturada una
 * línea no se edita ni se elimina, solo se agrega y se consulta (por eso
 * implementa {@link RegistroRepository} y no {@link Repository}).
 */
public class DetalleVentaRepository implements RegistroRepository<DetalleVenta> {

    private final CsvReader lector = new CsvReader();
    private final CsvWriter escritor = new CsvWriter();

    /** {@inheritDoc} */
    @Override
    public List<DetalleVenta> obtenerTodos() {
        List<DetalleVenta> resultado = new ArrayList<>();
        for (String[] fila : lector.leer(Constantes.DETALLE_VENTAS_CSV)) {
            resultado.add(mapearDesdeFila(fila));
        }
        return resultado;
    }

    /** {@inheritDoc} */
    @Override
    public void guardar(DetalleVenta detalle) {
        escritor.agregarLinea(Constantes.DETALLE_VENTAS_CSV, mapearAFila(detalle));
    }

    /**
     * Obtiene las líneas de detalle correspondientes a una venta específica.
     *
     * @param idVenta id de la venta a filtrar
     * @return lista de líneas de esa venta, en el orden en que se guardaron
     */
    public List<DetalleVenta> obtenerPorVenta(String idVenta) {
        List<DetalleVenta> resultado = new ArrayList<>();
        for (DetalleVenta d : obtenerTodos()) {
            if (d.getIdVenta().equals(idVenta)) {
                resultado.add(d);
            }
        }
        return resultado;
    }

    private String[] mapearAFila(DetalleVenta d) {
        return new String[] {
                d.getIdVenta(), d.getCodigoProducto(),
                String.valueOf(d.getCantidad()), String.valueOf(d.getPrecioUnitario())
        };
    }

    private DetalleVenta mapearDesdeFila(String[] fila) {
        return new DetalleVenta(fila[0], fila[1], Integer.parseInt(fila[2]), Double.parseDouble(fila[3]));
    }
}
