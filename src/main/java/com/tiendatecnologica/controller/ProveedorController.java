package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.Proveedor;
import com.tiendatecnologica.service.ProveedorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

/**
 * Controlador de la vista de gestión de proveedores.
 *
 * Permite registrar, modificar, consultar y eliminar proveedores.
 */
public class ProveedorController {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtContacto;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TableView<Proveedor> tablaProveedores;

    @FXML
    private TableColumn<Proveedor, String> colId;

    @FXML
    private TableColumn<Proveedor, String> colNombre;

    @FXML
    private TableColumn<Proveedor, String> colContacto;

    @FXML
    private TableColumn<Proveedor, String> colTelefono;

    @FXML
    private TableColumn<Proveedor, String> colCorreo;

    private final ProveedorService proveedorService =
            new ProveedorService();

    private ObservableList<Proveedor> listaProveedores;

    /**
     * Inicializa la tabla y carga los proveedores registrados.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colContacto.setCellValueFactory(
                new PropertyValueFactory<>("contacto")
        );

        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono")
        );

        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo")
        );

        cargarDatos();

        /*
         * Cuando se selecciona un proveedor de la tabla,
         * sus datos se cargan automáticamente en los campos.
         */
        tablaProveedores.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        cargarProveedorSeleccionado(seleccionado);
                    }
                });
    }

    /**
     * Carga todos los proveedores desde el servicio.
     */
    private void cargarDatos() {
        listaProveedores = FXCollections.observableArrayList(
                proveedorService.obtenerProveedores()
        );

        tablaProveedores.setItems(listaProveedores);
    }

    /**
     * Registra un nuevo proveedor.
     */
    @FXML
    public void guardarProveedor() {
        try {
            Proveedor nuevo = obtenerProveedorDeCampos();

            proveedorService.registrarProveedor(nuevo);

            mostrarInformacion(
                    "Proveedor registrado correctamente."
            );

            cargarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Modifica el proveedor seleccionado en la tabla.
     *
     * El ID se conserva y solamente se actualizan los demás datos.
     */
    @FXML
    public void modificarProveedor() {
        try {
            Proveedor seleccionado =
                    tablaProveedores.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un proveedor de la tabla."
                );
            }

            Proveedor actualizado = new Proveedor(
                    seleccionado.getId(),
                    txtNombre.getText(),
                    txtContacto.getText(),
                    txtTelefono.getText(),
                    txtCorreo.getText()
            );

            proveedorService.actualizarProveedor(actualizado);

            mostrarInformacion(
                    "Proveedor modificado correctamente."
            );

            cargarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina el proveedor seleccionado en la tabla.
     */
    @FXML
    public void eliminarProveedor() {
        try {
            Proveedor seleccionado =
                    tablaProveedores.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un proveedor de la tabla."
                );
            }

            Alert confirmacion = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "¿Está seguro de eliminar al proveedor "
                            + seleccionado.getNombre() + "?"
            );

            confirmacion.setHeaderText("Eliminar proveedor");

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                proveedorService.eliminarProveedor(
                        seleccionado.getId()
                );

                mostrarInformacion(
                        "Proveedor eliminado correctamente."
                );

                cargarDatos();
                limpiarCampos();
            }

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Carga los datos del proveedor seleccionado en los campos.
     *
     * El ID se bloquea porque no debe modificarse.
     *
     * @param proveedor proveedor seleccionado
     */
    private void cargarProveedorSeleccionado(Proveedor proveedor) {
        txtId.setText(proveedor.getId());
        txtNombre.setText(proveedor.getNombre());
        txtContacto.setText(proveedor.getContacto());
        txtTelefono.setText(proveedor.getTelefono());
        txtCorreo.setText(proveedor.getCorreo());

        txtId.setDisable(true);
    }

    /**
     * Obtiene un proveedor utilizando los datos de los campos.
     *
     * @return proveedor creado con los datos ingresados
     */
    private Proveedor obtenerProveedorDeCampos() {
        return new Proveedor(
                txtId.getText().trim(),
                txtNombre.getText().trim(),
                txtContacto.getText().trim(),
                txtTelefono.getText().trim(),
                txtCorreo.getText().trim()
        );
    }

    /**
     * Limpia todos los campos del formulario.
     */
    @FXML
    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtCorreo.clear();

        txtId.setDisable(false);

        tablaProveedores.getSelectionModel().clearSelection();
    }

    /**
     * Muestra un mensaje de error.
     *
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(
                Alert.AlertType.ERROR,
                mensaje
        );

        alerta.setHeaderText("No se pudo completar la operación");
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje informativo.
     *
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarInformacion(String mensaje) {
        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION,
                mensaje
        );

        alerta.setHeaderText("Operación completada");
        alerta.showAndWait();
    }
}