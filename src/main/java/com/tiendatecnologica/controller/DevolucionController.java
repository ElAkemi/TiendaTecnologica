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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de gestión de devoluciones.
 *
 * Permite buscar una venta, seleccionar uno de sus productos,
 * registrar una solicitud de devolución y gestionar el estado
 * de las devoluciones registradas.
 *
 * La lógica de negocio se delega al {@link DevolucionService}.
 * El controlador se encarga de conectar la interfaz gráfica
 * con los servicios correspondientes.
 */
public class DevolucionController {

    /** Servicio encargado de gestionar las devoluciones. */
    private final DevolucionService devolucionService =
            new DevolucionService();

    /** Servicio utilizado para consultar las ventas y sus detalles. */
    private final VentaService ventaService =
            new VentaService();

    /**
     * Lista observable que contiene las devoluciones mostradas
     * en la tabla de historial.
     */
    private final ObservableList<Devolucion> devoluciones =
            FXCollections.observableArrayList();

    /** Campo para ingresar el identificador de la venta. */
    @FXML
    private TextField campoIdVenta;

    /** ComboBox que muestra los productos incluidos en la venta buscada. */
    @FXML
    private ComboBox<DetalleVenta> comboProductosVenta;

    /** Campo para ingresar la cantidad que se desea devolver. */
    @FXML
    private TextField campoCantidad;

    /** Campo para ingresar el motivo de la devolución. */
    @FXML
    private TextArea campoMotivo;

    /** Tabla que muestra el historial de devoluciones. */
    @FXML
    private TableView<Devolucion> tablaDevoluciones;

    /** Columna que muestra el identificador de la devolución. */
    @FXML
    private TableColumn<Devolucion, String> colDevolucionId;

    /** Columna que muestra el identificador de la venta. */
    @FXML
    private TableColumn<Devolucion, String> colDevolucionVenta;

    /** Columna que muestra el código del producto devuelto. */
    @FXML
    private TableColumn<Devolucion, String> colDevolucionProducto;

    /** Columna que muestra la cantidad solicitada para devolución. */
    @FXML
    private TableColumn<Devolucion, Number> colDevolucionCantidad;

    /** Columna que muestra la fecha de solicitud. */
    @FXML
    private TableColumn<Devolucion, String> colDevolucionFecha;

    /** Columna que muestra el estado actual de la devolución. */
    @FXML
    private TableColumn<Devolucion, String> colDevolucionEstado;

    /**
     * Inicializa el controlador.
     *
     * Configura las columnas de la tabla, establece el formato
     * utilizado para mostrar los productos de una venta y carga
     * las devoluciones registradas.
     */
    @FXML
    public void initialize() {

        colDevolucionId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colDevolucionVenta.setCellValueFactory(
                new PropertyValueFactory<>("idVenta")
        );

        colDevolucionProducto.setCellValueFactory(
                new PropertyValueFactory<>("codigoProducto")
        );

        colDevolucionCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colDevolucionFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaSolicitud")
        );

        colDevolucionEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        tablaDevoluciones.setItems(devoluciones);

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
                                + " (compró "
                                + item.getCantidad()
                                + ")"
                );
            }
        });

        comboProductosVenta.setButtonCell(
                comboProductosVenta.getCellFactory().call(null)
        );

        cargarDevoluciones();
    }

    /**
     * Busca una venta utilizando el identificador ingresado.
     *
     * Si la venta existe, carga sus productos en el ComboBox
     * para que el usuario pueda seleccionar cuál desea devolver.
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
     * Registra una nueva solicitud de devolución.
     *
     * Utiliza la venta y el producto seleccionados, junto con
     * la cantidad y el motivo indicados por el usuario.
     */
    @FXML
    public void solicitarDevolucion() {

        try {

            DetalleVenta seleccionado =
                    comboProductosVenta.getValue();

            if (seleccionado == null) {
                mostrarError(
                        "Busque una venta y seleccione un producto"
                );
                return;
            }

            int cantidad =
                    Integer.parseInt(campoCantidad.getText());

            devolucionService.registrarDevolucion(
                    campoIdVenta.getText(),
                    seleccionado.getCodigoProducto(),
                    cantidad,
                    campoMotivo.getText()
            );

            campoCantidad.clear();
            campoMotivo.clear();

            cargarDevoluciones();

            mostrarInfo(
                    "Solicitud de devolución registrada"
            );

        } catch (NumberFormatException e) {

            mostrarError(
                    "Indique una cantidad entera válida"
            );

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Cambia la devolución seleccionada al estado EN_REVISION.
     */
    @FXML
    public void enviarARevision() {
        avanzarSeleccionada(EstadoDevolucion.EN_REVISION);
    }

    /**
     * Cambia la devolución seleccionada al estado APROBADA.
     *
     * Al aprobarse, el servicio se encarga de realizar
     * el reintegro correspondiente al inventario.
     */
    @FXML
    public void aprobarDevolucion() {
        avanzarSeleccionada(EstadoDevolucion.APROBADA);
    }

    /**
     * Cambia la devolución seleccionada al estado RECHAZADA.
     */
    @FXML
    public void rechazarDevolucion() {
        avanzarSeleccionada(EstadoDevolucion.RECHAZADA);
    }

    /**
     * Cambia la devolución seleccionada al estado FINALIZADA.
     */
    @FXML
    public void finalizarDevolucion() {
        avanzarSeleccionada(EstadoDevolucion.FINALIZADA);
    }

    /**
     * Recarga la tabla de devoluciones desde el servicio.
     */
    @FXML
    public void cargarDevoluciones() {

        devoluciones.setAll(
                devolucionService.listarDevoluciones()
        );
    }

    /**
     * Cambia el estado de la devolución seleccionada.
     *
     * El servicio valida si la transición de estado solicitada
     * es válida antes de realizar el cambio.
     *
     * @param nuevoEstado nuevo estado al que se desea avanzar
     */
    private void avanzarSeleccionada(
            EstadoDevolucion nuevoEstado) {

        Devolucion seleccionada =
                tablaDevoluciones
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {
            mostrarError(
                    "Seleccione una devolución de la tabla"
            );
            return;
        }

        try {

            devolucionService.avanzarEstado(
                    seleccionada.getId(),
                    nuevoEstado
            );

            cargarDevoluciones();

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