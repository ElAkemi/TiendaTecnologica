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
        // TODO (Persona 2): cargar proveedores.fxml
    }

    @FXML
    public void abrirCompras() {
        // TODO (Persona 2): cargar compras.fxml
    }

    @FXML
    public void abrirPuntoVenta() {
        // TODO (Persona 3): cargar ventas.fxml
    }

    @FXML
    public void abrirGarantias() {
        // TODO (Persona 4): cargar garantias.fxml
    }

    @FXML
    public void abrirReportes() {
        // TODO (Persona 4): cargar reportes.fxml
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
