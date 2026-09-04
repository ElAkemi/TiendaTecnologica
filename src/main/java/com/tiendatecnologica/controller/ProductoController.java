package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductoController {

    private final ProductoService productoService = new ProductoService();

    @FXML private TextField campoCodigo;
    @FXML private TextField campoNombre;
    @FXML private TextField campoCategoria;
    @FXML private TextField campoMarca;
    @FXML private TextField campoPrecioCompra;
    @FXML private TextField campoPrecioVenta;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoStockMinimo;
    @FXML private TextField campoMesesGarantia;
    @FXML private TextField campoBusqueda;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, Number> colPrecioVenta;
    @FXML private TableColumn<Producto, Number> colCantidad;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadDisponible"));

        tablaProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarFormulario(seleccionado);
                    }
                });

        cargarProductos();
    }

    @FXML
    public void registrarProducto() {
        try {
            Producto producto = leerFormulario();
            productoService.registrarProducto(producto);
            cargarProductos();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void modificarProducto() {
        try {
            Producto producto = leerFormulario();
            productoService.modificarProducto(producto);
            cargarProductos();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione un producto de la tabla");
            return;
        }
        try {
            productoService.eliminarProducto(seleccionado.getCodigo());
            cargarProductos();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void buscarProducto() {
        String texto = campoBusqueda.getText();
        ObservableList<Producto> resultado = FXCollections.observableArrayList(
                productoService.buscarProductos(texto));
        tablaProductos.setItems(resultado);
    }

    @FXML
    public void cargarProductos() {
        ObservableList<Producto> productos = FXCollections.observableArrayList(
                productoService.listarProductos());
        tablaProductos.setItems(productos);
    }

    @FXML
    public void limpiarFormulario() {
        campoCodigo.clear();
        campoNombre.clear();
        campoCategoria.clear();
        campoMarca.clear();
        campoPrecioCompra.clear();
        campoPrecioVenta.clear();
        campoCantidad.clear();
        campoStockMinimo.clear();
        campoMesesGarantia.clear();
        tablaProductos.getSelectionModel().clearSelection();
    }

    private void cargarFormulario(Producto p) {
        campoCodigo.setText(p.getCodigo());
        campoNombre.setText(p.getNombre());
        campoCategoria.setText(p.getCategoria());
        campoMarca.setText(p.getMarca());
        campoPrecioCompra.setText(String.valueOf(p.getPrecioCompra()));
        campoPrecioVenta.setText(String.valueOf(p.getPrecioVenta()));
        campoCantidad.setText(String.valueOf(p.getCantidadDisponible()));
        campoStockMinimo.setText(String.valueOf(p.getStockMinimo()));
        campoMesesGarantia.setText(String.valueOf(p.getMesesGarantia()));
    }

    private Producto leerFormulario() {
        return new Producto(
                campoCodigo.getText(),
                campoNombre.getText(),
                campoCategoria.getText(),
                campoMarca.getText(),
                Double.parseDouble(campoPrecioCompra.getText()),
                Double.parseDouble(campoPrecioVenta.getText()),
                Integer.parseInt(campoCantidad.getText()),
                Integer.parseInt(campoStockMinimo.getText()),
                Integer.parseInt(campoMesesGarantia.getText())
        );
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.showAndWait();
    }
}
