package com.tiendatecnologica.controller;

import com.tiendatecnologica.enums.EstadoGarantia;
import com.tiendatecnologica.model.DetalleVenta;
import com.tiendatecnologica.model.Garantia;
import com.tiendatecnologica.service.GarantiaService;
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
 * Controlador de la pantalla de Garantías. Permite buscar los productos de
 * una venta, solicitar una garantía sobre uno de ellos y avanzar el estado
 * de las solicitudes ya registradas. Toda la validación y las reglas de
 * negocio viven en {@link GarantiaService}; este controlador solo conecta
 * la interfaz gráfica con el servicio.
 */
public class GarantiaController {

    private final GarantiaService garantiaService = new GarantiaService();
    private final VentaService ventaService = new VentaService();
    private final ObservableList<Garantia> garantias = FXCollections.observableArrayList();

    // --- Búsqueda de venta y selección de producto ---
    @FXML private TextField campoIdVenta;
    @FXML private Button botonBuscarVenta;
    @FXML private ComboBox<DetalleVenta> comboProductosVenta;
    @FXML private TextArea campoDescripcionProblema;

    // --- Historial de garantías ---
    @FXML private TableView<Garantia> tablaGarantias;
    @FXML private TableColumn<Garantia, String> colGarantiaId;
    @FXML private TableColumn<Garantia, String> colGarantiaVenta;
    @FXML private TableColumn<Garantia, String> colGarantiaProducto;
    @FXML private TableColumn<Garantia, String> colGarantiaFecha;
    @FXML private TableColumn<Garantia, String> colGarantiaEstado;

    /**
     * Inicializa las columnas de la tabla, el formato del combo de productos
     * y carga las garantías ya registradas. JavaFX llama este método
     * automáticamente después de inyectar los campos {@code @FXML}.
     */
    @FXML
    public void initialize() {
        colGarantiaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGarantiaVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colGarantiaProducto.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colGarantiaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSolicitud"));
        colGarantiaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaGarantias.setItems(garantias);

        comboProductosVenta.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DetalleVenta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getCodigoProducto() + " - " + item.getNombreProducto());
            }
        });
        comboProductosVenta.setButtonCell(comboProductosVenta.getCellFactory().call(null));

        cargarGarantias();
    }

    /**
     * Busca la venta indicada y carga sus líneas de producto en el combo,
     * para que el usuario elija sobre cuál producto reclamar garantía.
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
     * Registra una nueva solicitud de garantía para el producto seleccionado
     * de la venta buscada, con la descripción del problema indicada.
     */
    @FXML
    public void solicitarGarantia() {
        try {
            DetalleVenta seleccionado = comboProductosVenta.getValue();
            if (seleccionado == null) {
                mostrarError("Busque una venta y seleccione un producto");
                return;
            }
            garantiaService.registrarGarantia(
                    campoIdVenta.getText(), seleccionado.getCodigoProducto(),
                    campoDescripcionProblema.getText());
            campoDescripcionProblema.clear();
            cargarGarantias();
            mostrarInfo("Solicitud de garantía registrada");
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /** Avanza la garantía seleccionada al estado EN_REVISION. */
    @FXML
    public void enviarARevision() {
        avanzarSeleccionada(EstadoGarantia.EN_REVISION);
    }

    /** Avanza la garantía seleccionada al estado APROBADA. */
    @FXML
    public void aprobarGarantia() {
        avanzarSeleccionada(EstadoGarantia.APROBADA);
    }

    /** Avanza la garantía seleccionada al estado RECHAZADA. */
    @FXML
    public void rechazarGarantia() {
        avanzarSeleccionada(EstadoGarantia.RECHAZADA);
    }

    /** Avanza la garantía seleccionada al estado FINALIZADA. */
    @FXML
    public void finalizarGarantia() {
        avanzarSeleccionada(EstadoGarantia.FINALIZADA);
    }

    /**
     * Recarga la tabla de garantías desde el repositorio.
     */
    @FXML
    public void cargarGarantias() {
        garantias.setAll(garantiaService.listarGarantias());
    }

    /**
     * Avanza la garantía actualmente seleccionada en la tabla al estado
     * indicado, mostrando un error si no hay selección o si la transición
     * de estado no es válida.
     *
     * @param nuevoEstado estado al que se quiere avanzar
     */
    private void avanzarSeleccionada(EstadoGarantia nuevoEstado) {
        Garantia seleccionada = tablaGarantias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccione una garantía de la tabla");
            return;
        }
        try {
            garantiaService.avanzarEstado(seleccionada.getId(), nuevoEstado);
            cargarGarantias();
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
