package com.tiendatecnologica.repository;

import com.tiendatecnologica.enums.EstadoVenta;
import com.tiendatecnologica.enums.MetodoPago;
import com.tiendatecnologica.model.Pago;
import com.tiendatecnologica.model.Venta;
import com.tiendatecnologica.util.Constantes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//Lo mismo, se encarga de registrar a venta
/**
 * Repositorio de ventas. Único responsable de leer/escribir ventas.csv.
 * No hay un {@code Pago} ni un CSV de pagos por separado: los datos del
 * pago (método, monto recibido, vuelto) se guardan como columnas más de
 * la fila de venta, ya que un pago siempre pertenece a exactamente una venta.
 */
public class VentaRepository extends CsvRepository<Venta> {

    /** {@inheritDoc} */
    @Override
    protected String rutaArchivo() {
        return Constantes.VENTAS_CSV;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] encabezado() {
        return new String[] {
                "id", "fecha", "idCliente", "subtotal", "impuesto", "total",
                "metodoPago", "montoRecibido", "vuelto", "estado"
        };
    }

    /** {@inheritDoc} */
    @Override
    protected String[] mapearAFila(Venta v) {
        Pago pago = v.getPago();
        return new String[] {
                v.getId(),
                v.getFecha().toString(),
                v.getIdCliente(),
                String.valueOf(v.getSubtotal()),
                String.valueOf(v.getImpuesto()),
                String.valueOf(v.getTotal()),
                pago.getMetodoPago().name(),
                String.valueOf(pago.getMontoRecibido()),
                String.valueOf(pago.getVuelto()),
                v.getEstado().name()
        };
    }

    /** {@inheritDoc} */
    @Override
    protected Venta mapearDesdeFila(String[] fila) {
        String id = fila[0];
        LocalDateTime fecha = LocalDateTime.parse(fila[1]);
        String idCliente = fila[2];
        double subtotal = Double.parseDouble(fila[3]);
        double impuesto = Double.parseDouble(fila[4]);
        double total = Double.parseDouble(fila[5]);
        MetodoPago metodoPago = MetodoPago.valueOf(fila[6]);
        double montoRecibido = Double.parseDouble(fila[7]);
        double vuelto = Double.parseDouble(fila[8]);
        EstadoVenta estado = EstadoVenta.valueOf(fila[9]);

        Pago pago = Pago.reconstruir(metodoPago, montoRecibido, vuelto);
        return new Venta(id, fecha, idCliente, subtotal, impuesto, total, pago, estado);
    }

    /** {@inheritDoc} */
    @Override
    protected String obtenerClave(Venta v) {
        return v.getId();
    }

    /**
     * Obtiene las ventas asociadas a un cliente específico.
     *
     * @param idCliente id del cliente a filtrar
     * @return lista de ventas de ese cliente
     */
    public List<Venta> obtenerVentasPorCliente(String idCliente) {
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : obtenerTodos()) {
            if (v.getIdCliente().equals(idCliente)) {
                resultado.add(v);
            }
        }
        return resultado;
    }
}
