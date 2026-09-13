package com.tiendatecnologica.repository;

import com.tiendatecnologica.model.DetalleCompra;
import com.tiendatecnologica.model.OrdenCompra;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraRepository {
    private String rutaArchivoCompras = "data/compras.csv";
    private String rutaArchivoDetalles = "data/detalle_compras.csv";

    public List<OrdenCompra> listar() {
        List<OrdenCompra> lista = new ArrayList<>();
        File archivo = new File(rutaArchivoCompras);
        if (!archivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    String idOrden = datos[0];
                    String idProveedor = datos[1];
                    String fecha = datos[2];
                    String estado = datos[3];
                    
                    List<DetalleCompra> detalles = cargarDetalles(idOrden);
                    OrdenCompra orden = new OrdenCompra(idOrden, idProveedor, fecha, estado, detalles);
                    lista.add(orden);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private List<DetalleCompra> cargarDetalles(String idOrdenBuscada) {
        List<DetalleCompra> detalles = new ArrayList<>();
        File archivo = new File(rutaArchivoDetalles);
        if (!archivo.exists()) return detalles;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                String[] datos = linea.split(",");
                if (datos.length >= 4) {
                    String idOrden = datos[0];
                    if (idOrden.equals(idOrdenBuscada)) {
                        String idProducto = datos[1];
                        int cantidad = Integer.parseInt(datos[2]);
                        double costoUnitario = Double.parseDouble(datos[3]);
                        detalles.add(new DetalleCompra(idProducto, cantidad, costoUnitario));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    public void guardar(OrdenCompra orden) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivoCompras, true))) {
            pw.println(orden.getIdOrden() + "," + orden.getIdProveedor() + "," + 
                       orden.getFecha() + "," + orden.getEstado());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivoDetalles, true))) {
            if (orden.getDetalles() != null) {
                for (DetalleCompra detalle : orden.getDetalles()) {
                    pw.println(orden.getIdOrden() + "," + detalle.getIdProducto() + "," + 
                               detalle.getCantidad() + "," + detalle.getCostoUnitario());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
