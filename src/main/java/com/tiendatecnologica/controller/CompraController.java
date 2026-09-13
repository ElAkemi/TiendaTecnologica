package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.DetalleCompra;
import com.tiendatecnologica.model.OrdenCompra;
import com.tiendatecnologica.service.CompraService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class CompraController {

    @FXML
    private TextField txtIdOrden;

    @FXML
    private TextField txtIdProveedor;

    @FXML
    private TextField txtFecha;

    @FXML
    private TextField txtIdProducto;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TextField txtCostoUnitario;

    @FXML
    private TableView<OrdenCompra> tablaCompras;

    @FXML
    private TableColumn<OrdenCompra, String> colIdOrden;

    @FXML
    private TableColumn<OrdenCompra, String> colIdProveedor;

    @FXML
    private TableColumn<OrdenCompra, String> colFecha;

    @FXML
    private TableColumn<OrdenCompra, String> colEstado;

    @FXML
    private TableView<DetalleCompra> tablaDetalles;

    @FXML
    private TableColumn<DetalleCompra, String> colIdProducto;

    @FXML
    private TableColumn<DetalleCompra, Integer> colCantidad;

    @FXML
    private TableColumn<DetalleCompra, Double> colCostoUnitario;

    @FXML
    private TableColumn<DetalleCompra, Double> colSubtotal;

    private final CompraService servicio = new CompraService();

    private ObservableList<OrdenCompra> listaCompras;

    private final ObservableList<DetalleCompra> listaDetalles =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colIdOrden.setCellValueFactory(
                new PropertyValueFactory<>("idOrden"));

        colIdProveedor.setCellValueFactory(
                new PropertyValueFactory<>("idProveedor"));

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fecha"));

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado"));

        colIdProducto.setCellValueFactory(
                new PropertyValueFactory<>("idProducto"));

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad"));

        colCostoUnitario.setCellValueFactory(
                new PropertyValueFactory<>("costoUnitario"));

        colSubtotal.setCellValueFactory(
                datos -> new SimpleObjectProperty<>(
                        datos.getValue().calcularSubtotal()
                )
        );

        tablaDetalles.setItems(listaDetalles);

        cargarDatos();
    }

    /**
     * Carga las órdenes de compra almacenadas.
     */
    private void cargarDatos() {

        listaCompras =
                FXCollections.observableArrayList(
                        servicio.obtenerCompras()
                );

        tablaCompras.setItems(listaCompras);
    }

    /**
     * Agrega un detalle temporalmente a la orden que se está creando.
     */
    @FXML
    public void agregarDetalle() {

        String idProducto =
                txtIdProducto.getText().trim();

        String cantidadTexto =
                txtCantidad.getText().trim();

        String costoTexto =
                txtCostoUnitario.getText().trim();

        if (idProducto.isEmpty()
                || cantidadTexto.isEmpty()
                || costoTexto.isEmpty()) {

            System.out.println(
                    "Debe completar todos los datos del producto."
            );

            return;
        }

        try {

            int cantidad =
                    Integer.parseInt(cantidadTexto);

            double costoUnitario =
                    Double.parseDouble(costoTexto);

            DetalleCompra detalle =
                    new DetalleCompra(
                            idProducto,
                            cantidad,
                            costoUnitario
                    );

            listaDetalles.add(detalle);

            txtIdProducto.clear();
            txtCantidad.clear();
            txtCostoUnitario.clear();

        } catch (NumberFormatException e) {

            System.out.println(
                    "La cantidad debe ser un entero y el costo debe ser numérico."
            );
        }
    }

    /**
     * Registra una nueva orden de compra.
     *
     * El servicio se encarga de establecer el estado Pendiente.
     */
    @FXML
    public void guardarCompra() {

        String idOrden =
                txtIdOrden.getText().trim();

        String idProveedor =
                txtIdProveedor.getText().trim();

        String fecha =
                txtFecha.getText().trim();

        if (idOrden.isEmpty()
                || idProveedor.isEmpty()
                || fecha.isEmpty()) {

            System.out.println(
                    "Debe completar todos los datos de la orden."
            );

            return;
        }

        if (listaDetalles.isEmpty()) {

            System.out.println(
                    "La orden debe tener al menos un producto."
            );

            return;
        }

        List<DetalleCompra> detalles =
                new ArrayList<>(listaDetalles);

        /*
         * El estado se coloca inicialmente como Pendiente
         * dentro del servicio.
         */
        OrdenCompra nueva =
                new OrdenCompra(
                        idOrden,
                        idProveedor,
                        fecha,
                        "Pendiente",
                        detalles
                );

        try {

            servicio.registrarCompra(nueva);

            cargarDatos();
            limpiarCampos();

            System.out.println(
                    "Compra registrada correctamente como Pendiente."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(e.getMessage());
        }
    }

    /**
     * Marca como recibida la orden seleccionada.
     *
     * El servicio registra las entradas correspondientes
     * en el inventario.
     */
    @FXML
    public void recibirCompra() {

        OrdenCompra seleccionada =
                tablaCompras.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {

            System.out.println(
                    "Debe seleccionar una orden de compra."
            );

            return;
        }

        try {

            servicio.recibirCompra(
                    seleccionada.getIdOrden()
            );

            cargarDatos();

            System.out.println(
                    "Compra marcada como Recibida."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(e.getMessage());
        }
    }

    /**
     * Cancela la orden seleccionada.
     *
     * Una compra cancelada no modifica el inventario.
     */
    @FXML
    public void cancelarCompra() {

        OrdenCompra seleccionada =
                tablaCompras.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {

            System.out.println(
                    "Debe seleccionar una orden de compra."
            );

            return;
        }

        try {

            servicio.cancelarCompra(
                    seleccionada.getIdOrden()
            );

            cargarDatos();

            System.out.println(
                    "Compra cancelada correctamente."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(e.getMessage());
        }
    }

    /**
     * Limpia los campos utilizados para registrar una nueva orden.
     */
    private void limpiarCampos() {

        txtIdOrden.clear();
        txtIdProveedor.clear();
        txtFecha.clear();

        txtIdProducto.clear();
        txtCantidad.clear();
        txtCostoUnitario.clear();

        listaDetalles.clear();
    }
}