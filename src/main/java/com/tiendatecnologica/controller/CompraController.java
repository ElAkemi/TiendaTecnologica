package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.OrdenCompra;
import com.tiendatecnologica.repositorio.OrdenCompraRepositorio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CompraController {

    @FXML private TextField txtIdOrden;
    @FXML private TextField txtIdProveedor;
    @FXML private TextField txtFecha;
    @FXML private TextField txtEstado;

    @FXML private TableView<OrdenCompra> tablaCompras;
    @FXML private TableColumn<OrdenCompra, String> colIdOrden;
    @FXML private TableColumn<OrdenCompra, String> colIdProveedor;
    @FXML private TableColumn<OrdenCompra, String> colFecha;
    @FXML private TableColumn<OrdenCompra, String> colEstado;

    private OrdenCompraRepositorio repositorio = new OrdenCompraRepositorio();
    private ObservableList<OrdenCompra> listaCompras;

    @FXML
    public void initialize() {
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colIdProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarDatos();
    }

    private void cargarDatos() {
        listaCompras = FXCollections.observableArrayList(repositorio.listar());
        tablaCompras.setItems(listaCompras);
    }

    @FXML
    public void guardarCompra() {
        String idOrden = txtIdOrden.getText();
        String idProveedor = txtIdProveedor.getText();
        String fecha = txtFecha.getText();
        String estado = txtEstado.getText();

        if (!idOrden.isEmpty() && !idProveedor.isEmpty()) {
            OrdenCompra nueva = new OrdenCompra(idOrden, idProveedor, fecha, estado, null);
            repositorio.guardar(nueva);
            cargarDatos();
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        txtIdOrden.clear();
        txtIdProveedor.clear();
        txtFecha.clear();
        txtEstado.clear();
    }
}
