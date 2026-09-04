package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.MovimientoInventario;
import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.service.InventarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventarioController {

    private final InventarioService inventarioService = new InventarioService();

    @FXML private TextField campoCodigoProducto;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoMotivo;

    @FXML private TableView<MovimientoInventario> tablaMovimientos;
    @FXML private TableColumn<MovimientoInventario, String> colId;
    @FXML private TableColumn<MovimientoInventario, String> colProducto;
    @FXML private TableColumn<MovimientoInventario, String> colTipo;
    @FXML private TableColumn<MovimientoInventario, Number> colCantidad;
    @FXML private TableColumn<MovimientoInventario, String> colFecha;
    @FXML private TableColumn<MovimientoInventario, String> colMotivo;

    @FXML private TableView<Producto> tablaAlertas;
    @FXML private TableColumn<Producto, String> colAlertaCodigo;
    @FXML private TableColumn<Producto, String> colAlertaNombre;
    @FXML private TableColumn<Producto, Number> colAlertaStock;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        colAlertaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colAlertaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAlertaStock.setCellValueFactory(new PropertyValueFactory<>("cantidadDisponible"));

        cargarMovimientos();
        cargarAlertas();
    }

    @FXML
    public void registrarEntrada() {
        try {
            inventarioService.registrarEntrada(
                    campoCodigoProducto.getText(),
                    Integer.parseInt(campoCantidad.getText()),
                    campoMotivo.getText());
            despuesDeMovimiento();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void registrarSalida() {
        try {
            inventarioService.registrarSalida(
                    campoCodigoProducto.getText(),
                    Integer.parseInt(campoCantidad.getText()),
                    campoMotivo.getText());
            despuesDeMovimiento();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void realizarAjuste() {
        try {
            inventarioService.ajustarInventario(
                    campoCodigoProducto.getText(),
                    Integer.parseInt(campoCantidad.getText()),
                    campoMotivo.getText());
            despuesDeMovimiento();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void cargarMovimientos() {
        ObservableList<MovimientoInventario> movimientos = FXCollections.observableArrayList(
                inventarioService.obtenerMovimientos());
        tablaMovimientos.setItems(movimientos);
    }

    @FXML
    public void cargarAlertas() {
        ObservableList<Producto> alertas = FXCollections.observableArrayList(
                inventarioService.obtenerProductosBajoStock());
        alertas.addAll(inventarioService.obtenerProductosAgotados());
        tablaAlertas.setItems(alertas);
    }

    @FXML
    public void filtrarMovimientos() {
        String codigo = campoCodigoProducto.getText();
        if (codigo == null || codigo.isBlank()) {
            cargarMovimientos();
            return;
        }
        ObservableList<MovimientoInventario> movimientos = FXCollections.observableArrayList(
                inventarioService.obtenerMovimientosProducto(codigo));
        tablaMovimientos.setItems(movimientos);
    }

    private void despuesDeMovimiento() {
        cargarMovimientos();
        cargarAlertas();
        campoCantidad.clear();
        campoMotivo.clear();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText("No se pudo registrar el movimiento");
        alerta.showAndWait();
    }
}
