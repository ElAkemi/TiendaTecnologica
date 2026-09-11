package com.tiendatecnologica.controller;

import com.tiendatecnologica.enums.EstadoDevolucion;
import com.tiendatecnologica.model.DetalleVenta;
import com.tiendatecnologica.model.Devolucion;
import com.tiendatecnologica.service.DevolucionService;
import com.tiendatecnologica.service.VentaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de Devoluciones. Permite buscar los productos
 * de una venta, solicitar la devolución de una cantidad de uno de ellos y
 * avanzar el estado de las solicitudes ya registradas. Toda la validación,
 * el control de cantidades y el reintegro de inventario al aprobar viven en
 * {@link DevolucionService}; este controlador solo conecta la interfaz
 * gráfica con el servicio.
 */
public class DevolucionController {

    private final DevolucionService devolucionService = new DevolucionService();
    private final VentaService ventaService = new VentaService();
    private final ObservableList<Devolucion> devoluciones = FXCollections.observableArrayList();

    // --- Búsqueda de venta y selección de producto ---
    @FXML private TextField campoIdVenta;
    @FXML private ComboBox<DetalleVenta> comboProductosVenta;
    @FXML private TextField campoCantidad;
    @FXML private TextArea campoMotivo;

    // --- Historial de devoluciones ---
    @FXML private TableView<Devolucion> tablaDevoluciones;
    @FXML private TableColumn<Devolucion, String> colDevolucionId;
    @FXML private TableColumn<Devolucion, String> colDevolucionVenta;
    @FXML private TableColumn<Devolucion, String> colDevolucionProducto;
    @FXML private TableColumn<Devolucion, Number> colDevolucionCantidad;
    @FXML private TableColumn<Devolucion, String> colDevolucionFecha;
    @FXML private TableColumn<Devolucion, String> colDevolucionEstado;

    /**
     * Inicializa las columnas de la tabla, el formato del combo de productos
     * y carga las devoluciones ya registradas. JavaFX llama este método
     * automáticamente después de inyectar los campos {@code @FXML}.
     */
    @FXML
    public void initialize() {
        colDevolucionId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDevolucionVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colDevolucionProducto.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colDevolucionCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDevolucionFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));
        colDevolucionEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaDevoluciones.setItems(devoluciones);

        comboProductosVenta.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DetalleVenta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getCodigoProducto() + " - " + item.getNombreProducto()
                                + " (compró " + item.getCantidad() + ")");
            }
        });
        comboProductosVenta.setButtonCell(comboProductosVenta.getCellFactory().call(null));

        cargarDevoluciones();
    }

    /**
     * Busca la venta indicada y carga sus líneas de producto en el combo,
     * para que el usuario elija sobre cuál producto solicitar la devolución.
     */
    @FXML
    public void buscarVenta() {
        String idVenta = campoIdVenta.getText();
        if (idVenta == null || idVenta.isBlank()) {
            mostrarError("Indique el id de una venta");
            return;
        }
        if (ventaService.buscarVenta(idVenta) == null) {
            mostrarError("No existe la venta " + idVenta);
            comboProductosVenta.setItems(FXCollections.observableArrayList());
            return;
        }
        comboProductosVenta.setItems(FXCollections.observableArrayList(ventaService.obtenerDetalle(idVenta)));
    }

    /**
     * Registra una nueva solicitud de devolución para el producto y la
     * cantidad indicados, sobre la venta buscada.
     */
    @FXML
    public void solicitarDevolucion() {
        try {
            DetalleVenta seleccionado = comboProductosVenta.getValue();
            if (seleccionado == null) {
                mostrarError("Busque una venta y seleccione un producto");
                return;
            }
            int cantidad = Integer.parseInt(campoCantidad.getText());
            devolucionService.registrarDevolucion(
                    campoIdVenta.getText(), seleccionado.getCodigoProducto(), cantidad, campoMotivo.getText());
            campoCantidad.clear();
            campoMotivo.clear();
            cargarDevoluciones();
            mostrarInfo("Solicitud de devolución registrada");
        } catch (NumberFormatException e) {
            mostrarError("Indique una cantidad entera válida");
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /** Avanza la devolución seleccionada al estado EN_REVISION. */
    @FXML
    public void enviarARevision() {
        avanzarSeleccionada(EstadoDevolucion.EN_REVISION);
    }

    /** Avanza la devolución seleccionada al estado APROBADA (reintegra inventario). */
    @FXML
    public void aprobarDevolucion() {
        avanzarSeleccionada(EstadoDevolucion.APROBADA);
    }

    /** Avanza la devolución seleccionada al estado RECHAZADA. */
    @FXML
    public void rechazarDevolucion() {
        avanzarSeleccionada(EstadoDevolucion.RECHAZADA);
    }

    /** Avanza la devolución seleccionada al estado FINALIZADA. */
    @FXML
    public void finalizarDevolucion() {
        avanzarSeleccionada(EstadoDevolucion.FINALIZADA);
    }

    /**
     * Recarga la tabla de devoluciones desde el repositorio.
     */
    @FXML
    public void cargarDevoluciones() {
        devoluciones.setAll(devolucionService.listarDevoluciones());
    }

    /**
     * Avanza la devolución actualmente seleccionada en la tabla al estado
     * indicado, mostrando un error si no hay selección o si la transición
     * de estado no es válida.
     *
     * @param nuevoEstado estado al que se quiere avanzar
     */
    private void avanzarSeleccionada(EstadoDevolucion nuevoEstado) {
        Devolucion seleccionada = tablaDevoluciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccione una devolución de la tabla");
            return;
        }
        try {
            devolucionService.avanzarEstado(seleccionada.getId(), nuevoEstado);
            cargarDevoluciones();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
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
