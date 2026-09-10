package com.tiendatecnologica.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Controlador del menú principal. Cada método navega a la vista
 * correspondiente cargando su FXML dentro del centro del BorderPane.
 */
public class MainController {

    @FXML
    private BorderPane contenedorPrincipal;
   
   @FXML
    public void abrirProductos() {
        cargarVista("/fxml/productos.fxml");
    }

    @FXML
    public void abrirInventario() {
        cargarVista("/fxml/inventario.fxml");
    }

    @FXML
    public void abrirProveedores() {
        cargarVista("/fxml/proveedores.fxml");
    }

    @FXML
    public void abrirCompras() {
        cargarVista("/fxml/compras.fxml");
    }

    @FXML
    public void abrirPuntoVenta() {
        cargarVista("/fxml/ventas.fxml");
    }

    @FXML
    public void abrirGarantias() {
        cargarVista("/fxml/garantias.fxml");
    }

    @FXML
    public void abrirDevoluciones() {
        cargarVista("/fxml/devoluciones.fxml");
    }

    @FXML
    public void abrirReportes() {
        cargarVista("/fxml/reportes.fxml");
    }

    private void cargarVista(String rutaFxml) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaFxml));
            contenedorPrincipal.setCenter(vista);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + rutaFxml, e);
        }
    }
}
