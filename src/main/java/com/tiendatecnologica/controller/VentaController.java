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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Controlador del punto de venta. Arma el carrito en pantalla
 * y delega las operaciones a {@link VentaService} y {@link ClienteService}.
 */
public class VentaController {

    private final ClienteService clienteService = new ClienteService();
    private final VentaService ventaService = new VentaService();
    private final ProductoService productoService = new ProductoService();
    private final ObservableList<DetalleVenta> carrito = FXCollections.observableArrayList();

    /** Cliente ya encontrado o registrado para la venta en curso. */
    private Cliente clienteActual;

    // --- Cliente ---
    @FXML private TextField campoCedulaCliente;
    @FXML private Label labelClienteInfo;
    @FXML private TextField campoNombreCliente;
    @FXML private TextField campoTelefonoCliente;
    @FXML private TextField campoEmailCliente;

    // --- Carrito ---
    @FXML private ComboBox<Producto> comboProductos;
    @FXML private TextField campoCantidadProducto;
    @FXML private TableView<DetalleVenta> tablaCarrito;
    @FXML private TableColumn<DetalleVenta, String> colCarritoProducto;
    @FXML private TableColumn<DetalleVenta, Number> colCarritoCantidad;
    @FXML private TableColumn<DetalleVenta, Number> colCarritoPrecio;
    @FXML private TableColumn<DetalleVenta, Number> colCarritoSubtotal;

    // --- Totales y pago ---
    @FXML private Label labelSubtotal;
    @FXML private Label labelImpuesto;
    @FXML private Label labelTotal;
    @FXML private ComboBox<MetodoPago> comboMetodoPago;
    @FXML private TextField campoMontoRecibido;
    @FXML private Label labelVuelto;

    // --- Historial de ventas ---
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String> colVentaId;
    @FXML private TableColumn<Venta, String> colVentaFecha;
    @FXML private TableColumn<Venta, String> colVentaCliente;
    @FXML private TableColumn<Venta, Number> colVentaTotal;
    @FXML private TableColumn<Venta, String> colVentaEstado;

    @FXML
    public void initialize() {
        colCarritoProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCarritoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCarritoPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCarritoSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tablaCarrito.setItems(carrito);

        colVentaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVentaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colVentaCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colVentaTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colVentaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        comboMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        comboMetodoPago.getSelectionModel().selectFirst();

        // Formato visual de los items del ComboBox
        comboProductos.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCodigo() + " - " + item.getNombre());
                }
            }
        });
        comboProductos.setButtonCell(comboProductos.getCellFactory().call(null));

        cargarProductos();
        campoMontoRecibido.textProperty().addListener((obs, anterior, nuevo) -> actualizarVuelto());
        actualizarTotales();
        cargarVentas();
    }

    public void cargarProductos() {
        comboProductos.setItems(FXCollections.observableArrayList(productoService.listarProductos()));
    }

    @FXML
    public void buscarCliente() {
        String cedula = campoCedulaCliente.getText();
        Cliente encontrado = clienteService.buscarPorCedula(cedula);
        if (encontrado == null) {
            clienteActual = null;
            labelClienteInfo.setText("No encontrado. Complete los datos y presione \"Registrar cliente\".");
        } else {
            clienteActual = encontrado;
            labelClienteInfo.setText(encontrado.getNombre() + " (" + encontrado.getId() + ")");
        }
    }

    @FXML
    public void registrarCliente() {
        try {
            Cliente nuevo = clienteService.registrarCliente(
                    campoCedulaCliente.getText(),
                    campoNombreCliente.getText(),
                    campoTelefonoCliente.getText(),
                    campoEmailCliente.getText());
            clienteActual = nuevo;
            labelClienteInfo.setText(nuevo.getNombre() + " (" + nuevo.getId() + ")");
            campoNombreCliente.clear();
            campoTelefonoCliente.clear();
            campoEmailCliente.clear();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void agregarAlCarrito() {
        try {
            Producto productoSeleccionado = comboProductos.getValue();
            if (productoSeleccionado == null) {
                mostrarError("Seleccione un producto del catálogo");
                return;
            }
            int cantidad = Integer.parseInt(campoCantidadProducto.getText());
            DetalleVenta item = ventaService.crearItemCarrito(productoSeleccionado.getCodigo(), cantidad);
            carrito.add(item);
            actualizarTotales();

            comboProductos.getSelectionModel().clearSelection();
            campoCantidadProducto.clear();
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero válido");
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void quitarDelCarrito() {
        DetalleVenta seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione una línea del carrito");
            return;
        }
        carrito.remove(seleccionado);
        actualizarTotales();
    }

    @FXML
    public void vaciarCarrito() {
        carrito.clear();
        actualizarTotales();
    }

    @FXML
    public void confirmarVenta() {
        try {
            if (clienteActual == null) {
                mostrarError("Busque o registre un cliente antes de confirmar la venta");
                return;
            }
            if (carrito.isEmpty()) {
                mostrarError("El carrito está vacío");
                return;
            }
            double montoRecibido = Double.parseDouble(campoMontoRecibido.getText());
            MetodoPago metodoPago = comboMetodoPago.getValue();

            Venta venta = ventaService.registrarVenta(
                    clienteActual.getId(), new ArrayList<>(carrito), metodoPago, montoRecibido);

            mostrarInfo("Venta registrada: " + venta.getId()
                    + "\nTotal: " + formatear(venta.getTotal())
                    + "\nVuelto: " + formatear(venta.getPago().getVuelto()));

            limpiarVentaActual();
            cargarVentas();
            cargarProductos(); // Refresca el inventario en pantalla
        } catch (NumberFormatException e) {
            mostrarError("Indique un monto recibido válido");
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void cargarVentas() {
        tablaVentas.setItems(FXCollections.observableArrayList(ventaService.listarVentas()));
    }

    @FXML
    public void anularVentaSeleccionada() {
        Venta seleccionada = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccione una venta de la tabla");
            return;
        }
        try {
            ventaService.anularVenta(seleccionada.getId());
            cargarVentas();
            cargarProductos(); // Refresca el inventario restablecido
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void actualizarTotales() {
        double subtotal = 0;
        for (DetalleVenta item : carrito) {
            subtotal += item.getSubtotal();
        }
        double impuesto = subtotal * Constantes.IMPUESTO;
        double total = subtotal + impuesto;

        labelSubtotal.setText(formatear(subtotal));
        labelImpuesto.setText(formatear(impuesto));
        labelTotal.setText(formatear(total));
        actualizarVuelto();
    }

    private void actualizarVuelto() {
        try {
            double subtotal = 0;
            for (DetalleVenta item : carrito) {
                subtotal += item.getSubtotal();
            }
            double total = subtotal + (subtotal * Constantes.IMPUESTO);

            String textoRecibido = campoMontoRecibido.getText();
            if (textoRecibido == null || textoRecibido.trim().isEmpty()) {
                labelVuelto.setText("--");
                return;
            }

            double recibido = Double.parseDouble(textoRecibido.trim());
            labelVuelto.setText(formatear(Math.max(recibido - total, 0)));
        } catch (NumberFormatException e) {
            labelVuelto.setText("--");
        }
    }

    private void limpiarVentaActual() {
        carrito.clear();
        campoMontoRecibido.clear();
        campoCedulaCliente.clear();
        labelClienteInfo.setText("");
        clienteActual = null;
        actualizarTotales();}

    private String formatear(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alerta.setHeaderText("Listo");
        alerta.showAndWait();
    }
}