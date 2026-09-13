package com.tiendatecnologica.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Controlador del menú principal de la aplicación.
 *
 * Se encarga de controlar la navegación entre las diferentes
 * vistas del sistema. Cada opción del menú carga el archivo FXML
 * correspondiente dentro del centro del BorderPane principal.
 */
public class MainController {

    /**
     * Contenedor principal donde se muestran las diferentes vistas
     * de la aplicación.
     */
    @FXML
    private BorderPane contenedorPrincipal;

    /**
     * Abre la vista de gestión de productos.
     */
    @FXML
    public void abrirProductos() {
        cargarVista("/fxml/productos.fxml");
    }

    /**
     * Abre la vista de gestión de inventario.
     */
    @FXML
    public void abrirInventario() {
        cargarVista("/fxml/inventario.fxml");
    }

    /**
     * Abre la vista de gestión de proveedores.
     */
    @FXML
    public void abrirProveedores() {
        cargarVista("/fxml/proveedores.fxml");
    }

    /**
     * Abre la vista de gestión de órdenes de compra.
     */
    @FXML
    public void abrirCompras() {
        cargarVista("/fxml/compras.fxml");
    }

    /**
     * Abre la vista del punto de venta.
     */
    @FXML
    public void abrirPuntoVenta() {
        cargarVista("/fxml/ventas.fxml");
    }

    /**
     * Abre la vista de gestión de garantías.
     */
    @FXML
    public void abrirGarantias() {
        cargarVista("/fxml/garantias.fxml");
    }

    /**
     * Abre la vista de gestión de devoluciones.
     */
    @FXML
    public void abrirDevoluciones() {
        cargarVista("/fxml/devoluciones.fxml");
    }

    /**
     * Abre la vista de reportes.
     */
    @FXML
    public void abrirReportes() {
        cargarVista("/fxml/reportes.fxml");
    }

    /**
     * Carga una vista FXML y la coloca en el centro del
     * contenedor principal.
     *
     * @param rutaFxml ruta del archivo FXML que se desea cargar
     * @throws RuntimeException si ocurre un error al cargar la vista
     */
    private void cargarVista(String rutaFxml) {

        try {

            Parent vista =
                    FXMLLoader.load(
                            getClass().getResource(rutaFxml)
                    );

            contenedorPrincipal.setCenter(vista);

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo cargar la vista: " + rutaFxml,
                    e
            );
        }
    }
}