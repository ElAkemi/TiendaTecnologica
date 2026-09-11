package com.tiendatecnologica.repository;

import com.tiendatecnologica.enums.EstadoDevolucion;
import com.tiendatecnologica.model.Devolucion;
import com.tiendatecnologica.util.Constantes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de solicitudes de devolución. Único responsable de leer/escribir
 * devoluciones.csv. Hereda todo el CRUD de {@link CsvRepository}: a diferencia
 * de un historial puro, una devolución sí se actualiza (su estado avanza), por
 * eso extiende {@link CsvRepository} y no {@link RegistroRepository}.
 */
public class DevolucionRepository extends CsvRepository<Devolucion> {

    /** {@inheritDoc} */
    @Override
    protected String rutaArchivo() {
        return Constantes.DEVOLUCIONES_CSV;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] encabezado() {
        return new String[] {
                "id", "idVenta", "codigoProducto", "cantidad", "fechaSolicitud", "motivo", "estado"
        };
    }

    /** {@inheritDoc} */
    @Override
    protected String[] mapearAFila(Devolucion d) {
        return new String[] {
                d.getId(), d.getIdVenta(), d.getCodigoProducto(),
                String.valueOf(d.getCantidad()),
                d.getFechaSolicitud().toString(),
                // se reemplazan comas por punto y coma para no romper el formato CSV,
                // ya que el motivo lo escribe libremente el usuario
                d.getMotivo() == null ? "" : d.getMotivo().replace(",", ";"),
                d.getEstado().name()
        };
    }

    /** {@inheritDoc} */
    @Override
    protected Devolucion mapearDesdeFila(String[] fila) {
        return new Devolucion(
                fila[0], fila[1], fila[2],
                Integer.parseInt(fila[3]),
                LocalDateTime.parse(fila[4]),
                fila[5],
                EstadoDevolucion.valueOf(fila[6])
        );
    }

    /** {@inheritDoc} */
    @Override
    protected String obtenerClave(Devolucion d) {
        return d.getId();
    }

    /**
     * Obtiene las solicitudes de devolución asociadas a una venta específica.
     *
     * @param idVenta id de la venta a filtrar
     * @return lista de devoluciones de esa venta
     */
    public List<Devolucion> obtenerPorVenta(String idVenta) {
        List<Devolucion> resultado = new ArrayList<>();
        for (Devolucion d : obtenerTodos()) {
            if (d.getIdVenta().equals(idVenta)) {
                resultado.add(d);
            }
        }
        return resultado;
    }
}
