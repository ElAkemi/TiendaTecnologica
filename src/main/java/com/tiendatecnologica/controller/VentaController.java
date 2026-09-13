package com.tiendatecnologica.controller;

import com.tiendatecnologica.enums.MetodoPago;
import com.tiendatecnologica.model.Cliente;
import com.tiendatecnologica.model.DetalleVenta;
import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.model.Venta;
import com.tiendatecnologica.service.ClienteService;
import com.tiendatecnologica.service.ProductoService;
import com.tiendatecnologica.service.VentaService;
import com.tiendatecnologica.util.Constantes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Controlador de la pantalla del punto de venta.
 *
 * Permite buscar o registrar clientes, agregar productos al carrito,
 * calcular los totales de la venta, seleccionar el método de pago,
 * confirmar ventas y consultar o anular ventas registradas.
 *
 * La lógica de negocio se delega a {@link VentaService},
 * {@link ClienteService} y {@link ProductoService}.
 * El controlador se encarga de conectar la interfaz gráfica
 * con los servicios correspondientes.
 */
public class VentaController {

    /** Servicio encargado de gestionar los clientes. */
    private final ClienteService clienteService =
            new ClienteService();

    /** Servicio encargado de gestionar las ventas. */
    private final VentaService ventaService =
            new VentaService();

    /** Servicio encargado de consultar los productos. */
    private final ProductoService productoService =
            new ProductoService();

    /**
     * Lista observable que representa el carrito de la venta actual.
     */
    private final ObservableList<DetalleVenta> carrito =
            FXCollections.observableArrayList();

    /**
     * Cliente encontrado o registrado para la venta actual.
     *
     * Permanece en null mientras no se haya identificado un cliente.
     */
    private Cliente clienteActual;

    // --- Cliente ---

    /** Campo para ingresar la cédula del cliente. */
    @FXML
    private TextField campoCedulaCliente;

    /** Etiqueta que muestra la información del cliente encontrado. */
    @FXML
    private Label labelClienteInfo;

    /** Campo para ingresar el nombre del cliente. */
    @FXML
    private TextField campoNombreCliente;

    /** Campo para ingresar el teléfono del cliente. */
    @FXML
    private TextField campoTelefonoCliente;

    /** Campo para ingresar el correo electrónico del cliente. */
    @FXML
    private TextField campoEmailCliente;

    // --- Carrito ---

    /** ComboBox que muestra los productos disponibles. */
    @FXML
    private ComboBox<Producto> comboProductos;

    /** Campo para ingresar la cantidad del producto que se desea agregar. */
    @FXML
    private TextField campoCantidadProducto;

    /** Tabla que muestra los productos agregados al carrito. */
    @FXML
    private TableView<DetalleVenta> tablaCarrito;

    /** Columna que muestra el nombre del producto. */
    @FXML
    private TableColumn<DetalleVenta, String> colCarritoProducto;

    /** Columna que muestra la cantidad de productos. */
    @FXML
    private TableColumn<DetalleVenta, Number> colCarritoCantidad;

    /** Columna que muestra el precio unitario del producto. */
    @FXML
    private TableColumn<DetalleVenta, Number> colCarritoPrecio;

    /** Columna que muestra el subtotal de cada línea del carrito. */
    @FXML
    private TableColumn<DetalleVenta, Number> colCarritoSubtotal;

    // --- Totales y pago ---

    /** Etiqueta que muestra el subtotal de la venta. */
    @FXML
    private Label labelSubtotal;

    /** Etiqueta que muestra el impuesto calculado. */
    @FXML
    private Label labelImpuesto;

    /** Etiqueta que muestra el total de la venta. */
    @FXML
    private Label labelTotal;

    /** ComboBox que permite seleccionar el método de pago. */
    @FXML
    private ComboBox<MetodoPago> comboMetodoPago;

    /** Campo para ingresar el monto recibido del cliente. */
    @FXML
    private TextField campoMontoRecibido;

    /** Etiqueta que muestra el vuelto calculado. */
    @FXML
    private Label labelVuelto;

    // --- Historial de ventas ---

    /** Tabla que muestra las ventas registradas. */
    @FXML
    private TableView<Venta> tablaVentas;

    /** Columna que muestra el identificador de la venta. */
    @FXML
    private TableColumn<Venta, String> colVentaId;

    /** Columna que muestra la fecha de la venta. */
    @FXML
    private TableColumn<Venta, String> colVentaFecha;

    /** Columna que muestra el identificador del cliente. */
    @FXML
    private TableColumn<Venta, String> colVentaCliente;

    /** Columna que muestra el total de la venta. */
    @FXML
    private TableColumn<Venta, Number> colVentaTotal;

    /** Columna que muestra el estado de la venta. */
    @FXML
    private TableColumn<Venta, String> colVentaEstado;

    /**
     * Inicializa el controlador.
     *
     * Configura las columnas de las tablas, carga los métodos
     * de pago disponibles, establece el formato de los productos
     * del ComboBox y carga los productos y ventas registradas.
     */
    @FXML
    public void initialize() {

        colCarritoProducto.setCellValueFactory(
                new PropertyValueFactory<>("nombreProducto")
        );

        colCarritoCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colCarritoPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precioUnitario")
        );

        colCarritoSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subtotal")
        );

        tablaCarrito.setItems(carrito);

        colVentaId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colVentaFecha.setCellValueFactory(
                new PropertyValueFactory<>("fecha")
        );

        colVentaCliente.setCellValueFactory(
                new PropertyValueFactory<>("idCliente")
        );

        colVentaTotal.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );

        colVentaEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        comboMetodoPago.setItems(
                FXCollections.observableArrayList(
                        MetodoPago.values()
                )
        );

        comboMetodoPago
                .getSelectionModel()
                .selectFirst();

        /*
         * Personaliza la forma en que los productos aparecen
         * dentro del ComboBox para facilitar su identificación.
         */
        comboProductos.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(
                    Producto item,
                    boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(
                            item.getCodigo()
                                    + " - "
                                    + item.getNombre()
                    );
                }
            }
        });

        comboProductos.setButtonCell(
                comboProductos.getCellFactory().call(null)
        );

        cargarProductos();

        /*
         * Cada vez que cambia el monto recibido,
         * se actualiza automáticamente el vuelto.
         */
        campoMontoRecibido.textProperty().addListener(
                (obs, anterior, nuevo) -> actualizarVuelto()
        );

        actualizarTotales();
        cargarVentas();
    }

    /**
     * Carga los productos registrados en el ComboBox del punto de venta.
     */
    public void cargarProductos() {

        comboProductos.setItems(
                FXCollections.observableArrayList(
                        productoService.listarProductos()
                )
        );
    }

    /**
     * Busca un cliente utilizando su número de cédula.
     *
     * Si el cliente existe, se almacena como el cliente actual
     * de la venta. Si no existe, se informa al usuario para que
     * pueda registrarlo.
     */
    @FXML
    public void buscarCliente() {

        String cedula =
                campoCedulaCliente.getText();

        Cliente encontrado =
                clienteService.buscarPorCedula(cedula);

        if (encontrado == null) {

            clienteActual = null;

            labelClienteInfo.setText(
                    "No encontrado. Complete los datos y presione "
                            + "\"Registrar cliente\"."
            );

        } else {

            clienteActual = encontrado;

            labelClienteInfo.setText(
                    encontrado.getNombre()
                            + " ("
                            + encontrado.getId()
                            + ")"
            );
        }
    }

    /**
     * Registra un nuevo cliente utilizando los datos
     * ingresados en el formulario.
     *
     * El cliente registrado pasa a ser automáticamente
     * el cliente de la venta actual.
     */
    @FXML
    public void registrarCliente() {

        try {

            Cliente nuevo =
                    clienteService.registrarCliente(
                            campoCedulaCliente.getText(),
                            campoNombreCliente.getText(),
                            campoTelefonoCliente.getText(),
                            campoEmailCliente.getText()
                    );

            clienteActual = nuevo;

            labelClienteInfo.setText(
                    nuevo.getNombre()
                            + " ("
                            + nuevo.getId()
                            + ")"
            );

            campoNombreCliente.clear();
            campoTelefonoCliente.clear();
            campoEmailCliente.clear();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Agrega el producto seleccionado al carrito.
     *
     * La creación del detalle de venta y las validaciones
     * correspondientes son realizadas por el servicio.
     */
    @FXML
    public void agregarAlCarrito() {

        try {

            Producto productoSeleccionado =
                    comboProductos.getValue();

            if (productoSeleccionado == null) {

                mostrarError(
                        "Seleccione un producto del catálogo"
                );

                return;
            }

            int cantidad =
                    Integer.parseInt(
                            campoCantidadProducto.getText()
                    );

            DetalleVenta item =
                    ventaService.crearItemCarrito(
                            productoSeleccionado.getCodigo(),
                            cantidad
                    );

            carrito.add(item);

            actualizarTotales();

            comboProductos
                    .getSelectionModel()
                    .clearSelection();

            campoCantidadProducto.clear();

        } catch (NumberFormatException e) {

            mostrarError(
                    "La cantidad debe ser un número entero válido"
            );

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina del carrito el producto seleccionado.
     */
    @FXML
    public void quitarDelCarrito() {

        DetalleVenta seleccionado =
                tablaCarrito
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarError(
                    "Seleccione una línea del carrito"
            );

            return;
        }

        carrito.remove(seleccionado);

        actualizarTotales();
    }

    /**
     * Elimina todos los productos del carrito actual.
     */
    @FXML
    public void vaciarCarrito() {

        carrito.clear();

        actualizarTotales();
    }

    /**
     * Confirma y registra la venta actual.
     *
     * Verifica que exista un cliente y que el carrito no esté vacío.
     * Luego obtiene el monto recibido y el método de pago y delega
     * el registro de la venta al servicio.
     */
    @FXML
    public void confirmarVenta() {

        try {

            if (clienteActual == null) {

                mostrarError(
                        "Busque o registre un cliente antes "
                                + "de confirmar la venta"
                );

                return;
            }

            if (carrito.isEmpty()) {

                mostrarError(
                        "El carrito está vacío"
                );

                return;
            }

            double montoRecibido =
                    Double.parseDouble(
                            campoMontoRecibido.getText()
                    );

            MetodoPago metodoPago =
                    comboMetodoPago.getValue();

            Venta venta =
                    ventaService.registrarVenta(
                            clienteActual.getId(),
                            new ArrayList<>(carrito),
                            metodoPago,
                            montoRecibido
                    );

            mostrarInfo(
                    "Venta registrada: "
                            + venta.getId()
                            + "\nTotal: "
                            + formatear(venta.getTotal())
                            + "\nVuelto: "
                            + formatear(
                            venta.getPago().getVuelto()
                    )
            );

            limpiarVentaActual();

            cargarVentas();

            // Actualiza los productos después de modificar el inventario.
            cargarProductos();

        } catch (NumberFormatException e) {

            mostrarError(
                    "Indique un monto recibido válido"
            );

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Carga las ventas registradas en la tabla de historial.
     */
    @FXML
    public void cargarVentas() {

        tablaVentas.setItems(
                FXCollections.observableArrayList(
                        ventaService.listarVentas()
                )
        );
    }

    /**
     * Anula la venta seleccionada.
     *
     * El servicio se encarga de realizar las operaciones necesarias
     * para anular la venta y restablecer los productos al inventario.
     */
    @FXML
    public void anularVentaSeleccionada() {

        Venta seleccionada =
                tablaVentas
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mostrarError(
                    "Seleccione una venta de la tabla"
            );

            return;
        }

        try {

            ventaService.anularVenta(
                    seleccionada.getId()
            );

            cargarVentas();

            // Actualiza el inventario mostrado después de la anulación.
            cargarProductos();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Calcula y actualiza el subtotal, impuesto y total
     * de los productos que se encuentran en el carrito.
     */
    private void actualizarTotales() {

        double subtotal = 0;

        for (DetalleVenta item : carrito) {
            subtotal += item.getSubtotal();
        }

        double impuesto =
                subtotal * Constantes.IMPUESTO;

        double total =
                subtotal + impuesto;

        labelSubtotal.setText(
                formatear(subtotal)
        );

        labelImpuesto.setText(
                formatear(impuesto)
        );

        labelTotal.setText(
                formatear(total)
        );

        actualizarVuelto();
    }

    /**
     * Calcula y actualiza el vuelto utilizando el monto
     * recibido y el total actual de la venta.
     *
     * Si el monto recibido está vacío o no contiene un número
     * válido, se muestra "--".
     */
    private void actualizarVuelto() {

        try {

            double subtotal = 0;

            for (DetalleVenta item : carrito) {
                subtotal += item.getSubtotal();
            }

            double total =
                    subtotal
                            + (subtotal * Constantes.IMPUESTO);

            String textoRecibido =
                    campoMontoRecibido.getText();

            if (textoRecibido == null
                    || textoRecibido.trim().isEmpty()) {

                labelVuelto.setText("--");

                return;
            }

            double recibido =
                    Double.parseDouble(
                            textoRecibido.trim()
                    );

            labelVuelto.setText(
                    formatear(
                            Math.max(recibido - total, 0)
                    )
            );

        } catch (NumberFormatException e) {

            labelVuelto.setText("--");
        }
    }

    /**
     * Limpia los datos de la venta actual después de
     * completar una operación.
     *
     * También elimina el cliente actual y actualiza
     * los totales mostrados.
     */
    private void limpiarVentaActual() {

        carrito.clear();

        campoMontoRecibido.clear();

        campoCedulaCliente.clear();

        labelClienteInfo.setText("");

        clienteActual = null;

        actualizarTotales();
    }

    /**
     * Formatea un valor numérico con dos posiciones decimales.
     *
     * @param valor valor que se desea formatear
     * @return valor formateado con dos decimales
     */
    private String formatear(double valor) {

        return String.format(
                Locale.US,
                "%.2f",
                valor
        );
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarError(String mensaje) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.ERROR,
                        mensaje
                );

        alerta.setHeaderText(
                "No se pudo completar la operación"
        );

        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje informativo al usuario.
     *
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarInfo(String mensaje) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION,
                        mensaje
                );

        alerta.setHeaderText("Listo");

        alerta.showAndWait();
    }
}