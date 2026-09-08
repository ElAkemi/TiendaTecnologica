package com.tiendatecnologica.repositorio;

import com.tiendatecnologica.model.Proveedor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorRepositorio {
    private String rutaArchivo = "data/proveedores.csv";

    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        File archivo = new File(rutaArchivo);
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
                if (datos.length >= 5) {
                    Proveedor p = new Proveedor(datos[0], datos[1], datos[2], datos[3], datos[4]);
                    lista.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardar(Proveedor proveedor) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo, true))) {
            pw.println(proveedor.getId() + "," + proveedor.getNombre() + "," + 
                       proveedor.getContacto() + "," + proveedor.getTelefono() + "," + proveedor.getCorreo());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
