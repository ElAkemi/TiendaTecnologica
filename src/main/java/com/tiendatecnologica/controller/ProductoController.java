package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de gestión de productos.
 *
 * Permite registrar, modificar, eliminar y consultar productos.
 * También permite seleccionar un producto de la tabla para cargar
 * sus datos en el formulario.
 *
 * La lógica de negocio se delega al {@link ProductoService}.
 * El controlador se encarga de conectar la interfaz gráfica
 * con el servicio.
 */
public class ProductoController {

    /** Servicio encargado de gestionar los productos. */
    private final ProductoService productoService =
            new ProductoService();

    /** Campo para ingresar el código del producto. */
    @FXML
    private TextField campoCodigo;

    /** Campo para ingresar el nombre del producto. */
    @FXML
    private TextField campoNombre;

    /** Campo para ingresar la categoría del producto. */
    @FXML
    private TextField campoCategoria;

    /** Campo para ingresar la marca del producto. */
    @FXML
    private TextField campoMarca;

    /** Campo para ingresar el precio de compra. */
    @FXML
    private TextField campoPrecioCompra;

    /** Campo para ingresar el precio de venta. */
    @FXML
    private TextField campoPrecioVenta;

    /** Campo para ingresar la cantidad disponible. */
    @FXML
    private TextField campoCantidad;

    /** Campo para ingresar el stock mínimo. */
    @FXML
    private TextField campoStockMinimo;

    /** Campo para ingresar la cantidad de meses de garantía. */
    @FXML
    private TextField campoMesesGarantia;

    /** Campo utilizado para buscar productos. */
    @FXML
    private TextField campoBusqueda;

    /** Tabla que muestra los productos registrados. */
    @FXML
    private TableView<Producto> tablaProductos;

    /** Columna que muestra el código del producto. */
    @FXML
    private TableColumn<Producto, String> colCodigo;

    /** Columna que muestra el nombre del producto. */
    @FXML
    private TableColumn<Producto, String> colNombre;

    /** Columna que muestra la categoría del producto. */
    @FXML
    private TableColumn<Producto, String> colCategoria;

    /** Columna que muestra la marca del producto. */
    @FXML
    private TableColumn<Producto, String> colMarca;

    /** Columna que muestra el precio de venta. */
    @FXML
    private TableColumn<Producto, Number> colPrecioVenta;

    /** Columna que muestra la cantidad disponible. */
    @FXML
    private TableColumn<Producto, Number> colCantidad;

    /**
     * Inicializa el controlador.
     *
     * Configura las columnas de la tabla, establece el evento
     * de selección de productos y carga los productos registrados.
     */
    @FXML
    public void initialize() {

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );

        colMarca.setCellValueFactory(
                new PropertyValueFactory<>("marca")
        );

        colPrecioVenta.setCellValueFactory(
                new PropertyValueFactory<>("precioVenta")
        );

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidadDisponible")
        );

        /*
         * Cuando el usuario selecciona un producto de la tabla,
         * sus datos se cargan automáticamente en el formulario.
         */
        tablaProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, anterior, seleccionado) -> {

                            if (seleccionado != null) {
                                cargarFormulario(seleccionado);
                            }
                        }
                );

        cargarProductos();
    }

    /**
     * Registra un nuevo producto.
     *
     * Los datos se obtienen del formulario y las validaciones
     * correspondientes son realizadas por el servicio.
     */
    @FXML
    public void registrarProducto() {

        try {

            Producto producto =
                    leerFormulario();

            productoService.registrarProducto(producto);

            cargarProductos();
            limpiarFormulario();

        } catch (NumberFormatException e) {

            mostrarError("Precio, cantidad, stock mínimo y meses de garantía deben ser números válidos");

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Modifica los datos del producto seleccionado.
     *
     * Los datos actualizados se obtienen del formulario y se
     * envían al servicio para realizar las validaciones.
     */
    @FXML
    public void modificarProducto() {

        try {

            Producto producto =
                    leerFormulario();

            productoService.modificarProducto(producto);

            cargarProductos();
            limpiarFormulario();

        } catch (NumberFormatException e) {

            mostrarError("Precio, cantidad, stock mínimo y meses de garantía deben ser números válidos");

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina el producto seleccionado de la tabla.
     */
    @FXML
    public void eliminarProducto() {

        Producto seleccionado =
                tablaProductos
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarError(
                    "Seleccione un producto de la tabla"
            );

            return;
        }

        try {

            productoService.eliminarProducto(
                    seleccionado.getCodigo()
            );

            cargarProductos();
            limpiarFormulario();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    /**
     * Busca productos utilizando el texto ingresado.
     *
     * El servicio realiza la búsqueda y la tabla se actualiza
     * con los resultados encontrados.
     */
    @FXML
    public void buscarProducto() {

        String texto =
                campoBusqueda.getText();

        ObservableList<Producto> resultado =
                FXCollections.observableArrayList(
                        productoService.buscarProductos(texto)
                );

        tablaProductos.setItems(resultado);
    }

    /**
     * Carga todos los productos registrados en la tabla.
     */
    @FXML
    public void cargarProductos() {

        ObservableList<Producto> productos =
                FXCollections.observableArrayList(
                        productoService.listarProductos()
                );

        tablaProductos.setItems(productos);
    }

    /**
     * Limpia todos los campos del formulario y elimina
     * la selección actual de la tabla.
     */
    @FXML
    public void limpiarFormulario() {

        campoCodigo.clear();
        campoNombre.clear();
        campoCategoria.clear();
        campoMarca.clear();
        campoPrecioCompra.clear();
        campoPrecioVenta.clear();
        campoCantidad.clear();
        campoStockMinimo.clear();
        campoMesesGarantia.clear();

        tablaProductos
                .getSelectionModel()
                .clearSelection();
    }

    /**
     * Carga los datos del producto seleccionado en el formulario.
     *
     * @param producto producto seleccionado en la tabla
     */
    private void cargarFormulario(Producto producto) {

        campoCodigo.setText(
                producto.getCodigo()
        );

        campoNombre.setText(
                producto.getNombre()
        );

        campoCategoria.setText(
                producto.getCategoria()
        );

        campoMarca.setText(
                producto.getMarca()
        );

        campoPrecioCompra.setText(
                String.valueOf(producto.getPrecioCompra())
        );

        campoPrecioVenta.setText(
                String.valueOf(producto.getPrecioVenta())
        );

        campoCantidad.setText(
                String.valueOf(producto.getCantidadDisponible())
        );

        campoStockMinimo.setText(
                String.valueOf(producto.getStockMinimo())
        );

        campoMesesGarantia.setText(
                String.valueOf(producto.getMesesGarantia())
        );
    }

    /**
     * Crea un objeto Producto utilizando los datos
     * ingresados en el formulario.
     *
     * La conversión de los campos numéricos se realiza
     * antes de enviar el producto al servicio.
     *
     * @return producto creado con los datos del formulario
     */
    private Producto leerFormulario() {

        return new Producto(
                campoCodigo.getText(),
                campoNombre.getText(),
                campoCategoria.getText(),
                campoMarca.getText(),
                Double.parseDouble(
                        campoPrecioCompra.getText()
                ),
                Double.parseDouble(
                        campoPrecioVenta.getText()
                ),
                Integer.parseInt(
                        campoCantidad.getText()
                ),
                Integer.parseInt(
                        campoStockMinimo.getText()
                ),
                Integer.parseInt(
                        campoMesesGarantia.getText()
                )
        );
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
}