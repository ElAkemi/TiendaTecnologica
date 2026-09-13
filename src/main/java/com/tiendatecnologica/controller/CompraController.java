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

/**
 * Controlador de la vista de gestión de compras.
 *
 * Permite registrar órdenes de compra, agregar sus detalles,
 * consultar las órdenes registradas y cambiar su estado a
 * Recibida o Cancelada.
 *
 * La lógica de negocio se delega al {@link CompraService}.
 */
public class CompraController {

    /** Campo para ingresar el identificador de la orden. */
    @FXML
    private TextField txtIdOrden;

    /** Campo para ingresar el identificador del proveedor. */
    @FXML
    private TextField txtIdProveedor;

    /** Campo para ingresar la fecha de la orden. */
    @FXML
    private TextField txtFecha;

    /** Campo para ingresar el identificador del producto. */
    @FXML
    private TextField txtIdProducto;

    /** Campo para ingresar la cantidad del producto. */
    @FXML
    private TextField txtCantidad;

    /** Campo para ingresar el costo unitario del producto. */
    @FXML
    private TextField txtCostoUnitario;

    /** Tabla que muestra las órdenes de compra registradas. */
    @FXML
    private TableView<OrdenCompra> tablaCompras;

    /** Columna que muestra el identificador de la orden. */
    @FXML
    private TableColumn<OrdenCompra, String> colIdOrden;

    /** Columna que muestra el identificador del proveedor. */
    @FXML
    private TableColumn<OrdenCompra, String> colIdProveedor;

    /** Columna que muestra la fecha de la orden. */
    @FXML
    private TableColumn<OrdenCompra, String> colFecha;

    /** Columna que muestra el estado de la orden. */
    @FXML
    private TableColumn<OrdenCompra, String> colEstado;

    /** Tabla que muestra los detalles de la orden que se está creando. */
    @FXML
    private TableView<DetalleCompra> tablaDetalles;

    /** Columna que muestra el identificador del producto. */
    @FXML
    private TableColumn<DetalleCompra, String> colIdProducto;

    /** Columna que muestra la cantidad solicitada. */
    @FXML
    private TableColumn<DetalleCompra, Integer> colCantidad;

    /** Columna que muestra el costo unitario. */
    @FXML
    private TableColumn<DetalleCompra, Double> colCostoUnitario;

    /** Columna que muestra el subtotal del detalle. */
    @FXML
    private TableColumn<DetalleCompra, Double> colSubtotal;

    /** Servicio encargado de gestionar las órdenes de compra. */
    private final CompraService servicio = new CompraService();

    /** Lista observable utilizada para mostrar las órdenes en la tabla. */
    private ObservableList<OrdenCompra> listaCompras;

    /**
     * Lista temporal de detalles de la orden que se está creando.
     *
     * Los detalles se almacenan aquí antes de registrar la orden
     * completa mediante el servicio.
     */
    private final ObservableList<DetalleCompra> listaDetalles =
            FXCollections.observableArrayList();

    /**
     * Inicializa el controlador.
     *
     * Configura las columnas de las tablas, establece la lista
     * temporal de detalles y carga las órdenes almacenadas.
     */
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

        /*
         * El subtotal no es un atributo almacenado directamente.
         * Se calcula a partir de la cantidad y el costo unitario
         * de cada detalle.
         */
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
     *
     * Obtiene las órdenes mediante el servicio y las coloca
     * en la tabla correspondiente.
     */
    private void cargarDatos() {

        listaCompras =
                FXCollections.observableArrayList(
                        servicio.obtenerCompras()
                );

        tablaCompras.setItems(listaCompras);
    }

    /**
     * Agrega un detalle a la orden que se está creando.
     *
     * El detalle permanece temporalmente en la lista hasta que
     * el usuario registra la orden completa.
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

            /*
             * Se limpian solamente los campos del detalle,
             * permitiendo ingresar otro producto a la misma orden.
             */
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
     * La orden se crea inicialmente en estado Pendiente.
     * Las validaciones y reglas de negocio son realizadas
     * por el {@link CompraService}.
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

        /*
         * Se crea una copia de la lista temporal para que la
         * orden tenga sus propios detalles.
         */
        List<DetalleCompra> detalles =
                new ArrayList<>(listaDetalles);

        /*
         * El estado inicial de una orden nueva es Pendiente.
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
     * El servicio se encarga de cambiar el estado de la orden
     * y registrar las entradas correspondientes en el inventario.
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
     * Cancela la orden de compra seleccionada.
     *
     * Una orden cancelada no modifica el inventario.
     * El servicio se encarga de validar que la transición
     * de estado sea permitida.
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
     *
     * También elimina los detalles que todavía no hayan sido
     * registrados dentro de una orden.
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