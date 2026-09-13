package com.tiendatecnologica.controller;

import com.tiendatecnologica.model.OrdenCompra;
import com.tiendatecnologica.model.Producto;
import com.tiendatecnologica.model.ProductoVendido;
import com.tiendatecnologica.model.Venta;
import com.tiendatecnologica.service.ReporteService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Locale;

/**
 * Controlador de la pantalla de reportes.
 *
 * Es de solo lectura: obtiene la información calculada por
 * {@link ReporteService} y la muestra en las diferentes tablas
 * y etiquetas de la interfaz.
 *
 * Los reportes incluyen información sobre ventas, productos más
 * vendidos, productos con bajo inventario, compras a proveedores
 * y ganancias estimadas.
 */
public class ReporteController {

    /** Servicio encargado de generar la información de los reportes. */
    private final ReporteService reporteService =
            new ReporteService();

    // --- Ventas ---

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

    // --- Productos más vendidos ---

    /** Tabla que muestra los productos con mayor cantidad de ventas. */
    @FXML
    private TableView<ProductoVendido> tablaMasVendidos;

    /** Columna que muestra el código del producto más vendido. */
    @FXML
    private TableColumn<ProductoVendido, String> colMasVendidoCodigo;

    /** Columna que muestra el nombre del producto más vendido. */
    @FXML
    private TableColumn<ProductoVendido, String> colMasVendidoNombre;

    /** Columna que muestra la cantidad vendida del producto. */
    @FXML
    private TableColumn<ProductoVendido, Number> colMasVendidoCantidad;

    // --- Bajo inventario ---

    /** Tabla que muestra los productos con bajo inventario. */
    @FXML
    private TableView<Producto> tablaBajoInventario;

    /** Columna que muestra el código del producto. */
    @FXML
    private TableColumn<Producto, String> colBajoInvCodigo;

    /** Columna que muestra el nombre del producto. */
    @FXML
    private TableColumn<Producto, String> colBajoInvNombre;

    /** Columna que muestra la cantidad disponible del producto. */
    @FXML
    private TableColumn<Producto, Number> colBajoInvDisponible;

    /** Columna que muestra el stock mínimo configurado. */
    @FXML
    private TableColumn<Producto, Number> colBajoInvMinimo;

    // --- Compras a proveedores ---

    /** Tabla que muestra las órdenes de compra registradas. */
    @FXML
    private TableView<OrdenCompra> tablaCompras;

    /** Columna que muestra el identificador de la orden de compra. */
    @FXML
    private TableColumn<OrdenCompra, String> colCompraId;

    /** Columna que muestra el identificador del proveedor. */
    @FXML
    private TableColumn<OrdenCompra, String> colCompraProveedor;

    /** Columna que muestra la fecha de la orden de compra. */
    @FXML
    private TableColumn<OrdenCompra, String> colCompraFecha;

    /** Columna que muestra el estado de la orden de compra. */
    @FXML
    private TableColumn<OrdenCompra, String> colCompraEstado;

    /** Columna que muestra el total de la orden de compra. */
    @FXML
    private TableColumn<OrdenCompra, Double> colCompraTotal;

    // --- Ganancias estimadas ---

    /** Etiqueta donde se muestra la ganancia estimada. */
    @FXML
    private Label labelGananciasEstimadas;

    /**
     * Inicializa el controlador.
     *
     * Configura las columnas de las diferentes tablas y carga
     * todos los reportes disponibles.
     */
    @FXML
    public void initialize() {

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

        colMasVendidoCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigoProducto")
        );

        colMasVendidoNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombreProducto")
        );

        colMasVendidoCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidadVendida")
        );

        colBajoInvCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );

        colBajoInvNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colBajoInvDisponible.setCellValueFactory(
                new PropertyValueFactory<>("cantidadDisponible")
        );

        colBajoInvMinimo.setCellValueFactory(
                new PropertyValueFactory<>("stockMinimo")
        );

        colCompraId.setCellValueFactory(
                new PropertyValueFactory<>("idOrden")
        );

        colCompraProveedor.setCellValueFactory(
                new PropertyValueFactory<>("idProveedor")
        );

        colCompraFecha.setCellValueFactory(
                new PropertyValueFactory<>("fecha")
        );

        colCompraEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        /*
         * OrdenCompra no tiene un método getTotal().
         * El total se obtiene mediante calcularTotal(), por lo que
         * esta columna debe configurarse manualmente.
         */
        colCompraTotal.setCellValueFactory(
                datos -> new SimpleDoubleProperty(
                        datos.getValue().calcularTotal()
                ).asObject()
        );

        cargarReportes();
    }

    /**
     * Recarga todos los reportes mostrados en la pantalla.
     *
     * Actualiza las tablas de ventas, productos más vendidos,
     * productos con bajo inventario y compras a proveedores.
     * También actualiza la ganancia estimada.
     */
    @FXML
    public void cargarReportes() {

        tablaVentas.setItems(
                FXCollections.observableArrayList(
                        reporteService.obtenerVentas()
                )
        );

        tablaMasVendidos.setItems(
                FXCollections.observableArrayList(
                        reporteService.obtenerProductosMasVendidos()
                )
        );

        tablaBajoInventario.setItems(
                FXCollections.observableArrayList(
                        reporteService.obtenerProductosBajoInventario()
                )
        );

        tablaCompras.setItems(
                FXCollections.observableArrayList(
                        reporteService.obtenerCompras()
                )
        );

        labelGananciasEstimadas.setText(
                String.format(
                        Locale.US,
                        "%.2f",
                        reporteService.calcularGananciasEstimadas()
                )
        );
    }
}