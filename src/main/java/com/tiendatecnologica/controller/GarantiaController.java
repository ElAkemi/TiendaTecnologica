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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de gestión de garantías.
 *
 * Permite buscar una venta, seleccionar uno de sus productos,
 * registrar una solicitud de garantía y gestionar el estado
 * de las garantías registradas.
 *
 * La lógica de negocio se delega al {@link GarantiaService}.
 * El controlador se encarga de conectar la interfaz gráfica
 * con los servicios correspondientes.
 */
public class GarantiaController {

    /** Servicio encargado de gestionar las garantías. */
    private final GarantiaService garantiaService =
            new GarantiaService();

    /** Servicio utilizado para consultar las ventas y sus detalles. */
    private final VentaService ventaService =
            new VentaService();

    /**
     * Lista observable que contiene las garantías mostradas
     * en la tabla de historial.
     */
    private final ObservableList<Garantia> garantias =
            FXCollections.observableArrayList();

    /** Campo para ingresar el identificador de la venta. */
    @FXML
    private TextField campoIdVenta;

    /** ComboBox que muestra los productos incluidos en la venta buscada. */
    @FXML
    private ComboBox<DetalleVenta> comboProductosVenta;

    /** Campo para describir el problema presentado por el producto. */
    @FXML
    private TextArea campoDescripcionProblema;

    /** Tabla que muestra el historial de garantías. */
    @FXML
    private TableView<Garantia> tablaGarantias;

    /** Columna que muestra el identificador de la garantía. */
    @FXML
    private TableColumn<Garantia, String> colGarantiaId;

    /** Columna que muestra el identificador de la venta. */
    @FXML
    private TableColumn<Garantia, String> colGarantiaVenta;

    /** Columna que muestra el código del producto. */
    @FXML
    private TableColumn<Garantia, String> colGarantiaProducto;

    /** Columna que muestra la fecha de solicitud. */
    @FXML
    private TableColumn<Garantia, String> colGarantiaFecha;

    /** Columna que muestra el estado actual de la garantía. */
    @FXML
    private TableColumn<Garantia, String> colGarantiaEstado;

    /**
     * Inicializa el controlador.
     *
     * Configura las columnas de la tabla, establece el formato
     * utilizado para mostrar los productos de una venta y carga
     * las garantías registradas.
     */
    @FXML
    public void initialize() {

        colGarantiaId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colGarantiaVenta.setCellValueFactory(
                new PropertyValueFactory<>("idVenta")
        );

        colGarantiaProducto.setCellValueFactory(
                new PropertyValueFactory<>("codigoProducto")
        );

        colGarantiaFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaSolicitud")
        );

        colGarantiaEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        tablaGarantias.setItems(garantias);

        /*
         * Personaliza la forma en que los productos aparecen
         * dentro del ComboBox para facilitar su identificación.
         */
        comboProductosVenta.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(
                    DetalleVenta item,
                    boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getCodigoProducto()
                                + " - "
                                + item.getNombreProducto()
                );
            }
        });

        comboProductosVenta.setButtonCell(
                comboProductosVenta.getCellFactory().call(null)
        );

        cargarGarantias();
    }

    /**
     * Busca una venta utilizando el identificador ingresado.
     *
     * Si la venta existe, carga sus productos en el ComboBox
     * para que el usuario pueda seleccionar cuál desea registrar
     * para garantía.
     */
    @FXML
    public void buscarVenta() {

        String idVenta =
                campoIdVenta.getText();

        if (idVenta == null || idVenta.isBlank()) {
            mostrarError("Indique el id de una venta");
            return;
        }

        if (ventaService.buscarVenta(idVenta) == null) {

            mostrarError(
                    "No existe la venta " + idVenta
            );

            comboProductosVenta.setItems(
                    FXCollections.observableArrayList()
            );

            return;
        }

        comboProductosVenta.setItems(
                FXCollections.observableArrayList(
                        ventaService.obtenerDetalle(idVenta)
                )
        );
    }

    /**
     * Registra una nueva solicitud de garantía.
     *
     * Utiliza la venta y el producto seleccionados junto con
     * la descripción del problema indicada por el usuario.
     */
    @FXML
    public void solicitarGarantia() {

        try {

            DetalleVenta seleccionado =
                    comboProductosVenta.getValue();

            if (seleccionado == null) {
                mostrarError(
                        "Busque una venta y seleccione un producto"
                );
                return;
            }

            garantiaService.registrarGarantia(
                    campoIdVenta.getText(),
                    seleccionado.getCodigoProducto(),
                    campoDescripcionProblema.getText()
            );

            campoDescripcionProblema.clear();

            cargarGarantias();

            mostrarInfo(
                    "Solicitud de garantía registrada"
            );

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Cambia la garantía seleccionada al estado EN_REVISION.
     */
    @FXML
    public void enviarARevision() {
        avanzarSeleccionada(EstadoGarantia.EN_REVISION);
    }

    /**
     * Cambia la garantía seleccionada al estado APROBADA.
     */
    @FXML
    public void aprobarGarantia() {
        avanzarSeleccionada(EstadoGarantia.APROBADA);
    }

    /**
     * Cambia la garantía seleccionada al estado RECHAZADA.
     */
    @FXML
    public void rechazarGarantia() {
        avanzarSeleccionada(EstadoGarantia.RECHAZADA);
    }

    /**
     * Cambia la garantía seleccionada al estado FINALIZADA.
     */
    @FXML
    public void finalizarGarantia() {
        avanzarSeleccionada(EstadoGarantia.FINALIZADA);
    }

    /**
     * Recarga la tabla de garantías desde el servicio.
     */
    @FXML
    public void cargarGarantias() {

        garantias.setAll(
                garantiaService.listarGarantias()
        );
    }

    /**
     * Cambia el estado de la garantía seleccionada.
     *
     * El servicio se encarga de validar si la transición
     * solicitada es válida.
     *
     * @param nuevoEstado nuevo estado al que se desea avanzar
     */
    private void avanzarSeleccionada(
            EstadoGarantia nuevoEstado) {

        Garantia seleccionada =
                tablaGarantias
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {
            mostrarError(
                    "Seleccione una garantía de la tabla"
            );
            return;
        }

        try {

            garantiaService.avanzarEstado(
                    seleccionada.getId(),
                    nuevoEstado
            );

            cargarGarantias();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(
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

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION,
                mensaje
        );

        alerta.setHeaderText("Listo");

        alerta.showAndWait();
    }
}