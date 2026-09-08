package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.Proveedor;
import com.tiendatecnologica.repositorio.ProveedorRepositorio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProveedorController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtContacto;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colId;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colContacto;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colCorreo;

    private ProveedorRepositorio repositorio = new ProveedorRepositorio();
    private ObservableList<Proveedor> listaProveedores;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        cargarDatos();
    }

    private void cargarDatos() {
        listaProveedores = FXCollections.observableArrayList(repositorio.listar());
        tablaProveedores.setItems(listaProveedores);
    }

    @FXML
    public void guardarProveedor() {
        String id = txtId.getText();
        String nombre = txtNombre.getText();
        String contacto = txtContacto.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();

        if (!id.isEmpty() && !nombre.isEmpty()) {
            Proveedor nuevo = new Proveedor(id, nombre, contacto, telefono, correo);
            repositorio.guardar(nuevo);
            cargarDatos();
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtCorreo.clear();
    }
}
