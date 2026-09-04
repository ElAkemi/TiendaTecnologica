package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.util.Constantes;

/**
 * Repositorio de productos. Único responsable de leer/escribir productos.csv.
 * Hereda todo el CRUD de {@link CsvRepository} y solo define cómo se
 * convierte un Producto hacia/desde una fila de CSV.
 */
public class ProductoRepository extends CsvRepository<Producto> {

    /** {@inheritDoc} */
    @Override
    protected String rutaArchivo() {
        return Constantes.PRODUCTOS_CSV;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] encabezado() {
        return new String[] {
                "codigo", "nombre", "categoria", "marca",
                "precioCompra", "precioVenta", "cantidadDisponible",
                "stockMinimo", "mesesGarantia"
        };
    }

    /** {@inheritDoc} */
    @Override
    protected String[] mapearAFila(Producto p) {
        return new String[] {
                p.getCodigo(), p.getNombre(), p.getCategoria(), p.getMarca(),
                String.valueOf(p.getPrecioCompra()), String.valueOf(p.getPrecioVenta()),
                String.valueOf(p.getCantidadDisponible()), String.valueOf(p.getStockMinimo()),
                String.valueOf(p.getMesesGarantia())
        };
    }

    /** {@inheritDoc} */
    @Override
    protected Producto mapearDesdeFila(String[] fila) {
        return new Producto(
                fila[0], fila[1], fila[2], fila[3],
                Double.parseDouble(fila[4]), Double.parseDouble(fila[5]),
                Integer.parseInt(fila[6]), Integer.parseInt(fila[7]), Integer.parseInt(fila[8])
        );
    }

    /** {@inheritDoc} */
    @Override
    protected String obtenerClave(Producto p) {
        return p.getCodigo();
    }
}
