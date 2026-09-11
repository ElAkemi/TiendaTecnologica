package com.tiendatecnologica.repository;

import com.tiendatecnologica.enums.EstadoGarantia;
import com.tiendatecnologica.model.Garantia;
import com.tiendatecnologica.util.Constantes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de solicitudes de garantía. Único responsable de leer/escribir
 * garantias.csv. Hereda todo el CRUD de {@link CsvRepository}: a diferencia
 * de un historial puro, una garantía sí se actualiza (su estado avanza), por
 * eso extiende {@link CsvRepository} y no {@link RegistroRepository}.
 */
public class GarantiaRepository extends CsvRepository<Garantia> {

    /** {@inheritDoc} */
    @Override
    protected String rutaArchivo() {
        return Constantes.GARANTIAS_CSV;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] encabezado() {
        return new String[] {
                "id", "idVenta", "codigoProducto", "fechaSolicitud", "descripcionProblema", "estado"
        };
    }

    /** {@inheritDoc} */
    @Override
    protected String[] mapearAFila(Garantia g) {
        return new String[] {
                g.getId(), g.getIdVenta(), g.getCodigoProducto(),
                g.getFechaSolicitud().toString(),
                // se reemplazan comas por punto y coma para no romper el formato CSV,
                // ya que la descripción la escribe libremente el usuario
                g.getDescripcionProblema() == null ? "" : g.getDescripcionProblema().replace(",", ";"),
                g.getEstado().name()
        };
    }

    /** {@inheritDoc} */
    @Override
    protected Garantia mapearDesdeFila(String[] fila) {
        return new Garantia(
                fila[0], fila[1], fila[2],
                LocalDateTime.parse(fila[3]),
                fila[4],
                EstadoGarantia.valueOf(fila[5])
        );
    }

    /** {@inheritDoc} */
    @Override
    protected String obtenerClave(Garantia g) {
        return g.getId();
    }

    /**
     * Obtiene las solicitudes de garantía asociadas a una venta específica.
     *
     * @param idVenta id de la venta a filtrar
     * @return lista de garantías de esa venta
     */
    public List<Garantia> obtenerPorVenta(String idVenta) {
        List<Garantia> resultado = new ArrayList<>();
        for (Garantia g : obtenerTodos()) {
            if (g.getIdVenta().equals(idVenta)) {
                resultado.add(g);
            }
        }
        return resultado;
    }
}
