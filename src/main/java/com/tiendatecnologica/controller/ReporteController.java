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
 * Controlador de la pantalla de Reportes. Es de solo lectura: toma la
 * información ya calculada por {@link ReporteService} (que a su vez agrega
 * datos de ventas, inventario y compras) y la muestra en tablas y etiquetas.
 */
public class ReporteController {

    private final ReporteService reporteService = new ReporteService();

    // --- Ventas ---
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String> colVentaId;
    @FXML private TableColumn<Venta, String> colVentaFecha;
    @FXML private TableColumn<Venta, String> colVentaCliente;
    @FXML private TableColumn<Venta, Number> colVentaTotal;
    @FXML private TableColumn<Venta, String> colVentaEstado;

    // --- Productos más vendidos ---
    @FXML private TableView<ProductoVendido> tablaMasVendidos;
    @FXML private TableColumn<ProductoVendido, String> colMasVendidoCodigo;
    @FXML private TableColumn<ProductoVendido, String> colMasVendidoNombre;
    @FXML private TableColumn<ProductoVendido, Number> colMasVendidoCantidad;

    // --- Bajo inventario ---
    @FXML private TableView<Producto> tablaBajoInventario;
    @FXML private TableColumn<Producto, String> colBajoInvCodigo;
    @FXML private TableColumn<Producto, String> colBajoInvNombre;
    @FXML private TableColumn<Producto, Number> colBajoInvDisponible;
    @FXML private TableColumn<Producto, Number> colBajoInvMinimo;

    // --- Compras a proveedores ---
    @FXML private TableView<OrdenCompra> tablaCompras;
    @FXML private TableColumn<OrdenCompra, String> colCompraId;
    @FXML private TableColumn<OrdenCompra, String> colCompraProveedor;
    @FXML private TableColumn<OrdenCompra, String> colCompraFecha;
    @FXML private TableColumn<OrdenCompra, String> colCompraEstado;
    @FXML private TableColumn<OrdenCompra, Double> colCompraTotal;

    // --- Ganancias estimadas ---
    @FXML private Label labelGananciasEstimadas;

    /**
     * Enlaza las columnas de cada tabla con las propiedades de sus modelos
     * y carga todos los reportes. JavaFX llama este método automáticamente
     * después de inyectar los campos {@code @FXML}.
     */
    @FXML
    public void initialize() {
        colVentaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVentaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colVentaCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colVentaTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colVentaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colMasVendidoCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colMasVendidoNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colMasVendidoCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));

        colBajoInvCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colBajoInvNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colBajoInvDisponible.setCellValueFactory(new PropertyValueFactory<>("cantidadDisponible"));
        colBajoInvMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        colCompraId.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colCompraProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colCompraFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCompraEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        // OrdenCompra no tiene getTotal(), sino calcularTotal(); se conecta a mano.
        colCompraTotal.setCellValueFactory(datos ->
                new SimpleDoubleProperty(datos.getValue().calcularTotal()).asObject());

        cargarReportes();
    }

    /**
     * Recarga los cuatro reportes (ventas, productos más vendidos, bajo
     * inventario y compras) y la ganancia estimada desde {@link ReporteService}.
     */
    @FXML
    public void cargarReportes() {
        tablaVentas.setItems(FXCollections.observableArrayList(reporteService.obtenerVentas()));
        tablaMasVendidos.setItems(FXCollections.observableArrayList(reporteService.obtenerProductosMasVendidos()));
        tablaBajoInventario.setItems(FXCollections.observableArrayList(reporteService.obtenerProductosBajoInventario()));
        tablaCompras.setItems(FXCollections.observableArrayList(reporteService.obtenerCompras()));
        labelGananciasEstimadas.setText(
                String.format(Locale.US, "%.2f", reporteService.calcularGananciasEstimadas()));
    }
}
