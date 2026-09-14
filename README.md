# Tienda Tecnologica - Sistema de Gestion de Inventario y Ventas

Sistema de escritorio desarrollado en JavaFX para la gestion integral de una tienda de tecnologia. La aplicacion implementa una arquitectura por capas (MVC / Servicio-Repositorio) y utiliza archivos en formato CSV para la persistencia de datos.

---

## Caracteristicas Principales

- Gestion de Productos y Proveedores: Registro, edicion y catalogo de productos tecnologicos y sus respectivos proveedores.
- Control de Inventarios: Movimientos de entrada, salida y ajustes de stock en tiempo real.
- Gestion de Ventas y Compras: Procesamiento de transacciones comerciales con clientes y ordenes de compra con proveedores.
- Garantias y Devoluciones: Seguimiento del ciclo de vida de garantias de productos y gestion de devoluciones con actualizacion de estados.
- Generacion de Reportes: Modulo dedicado para la emision de reportes y estadisticas operativas.
- Persistencia en CSV: Almacenamiento rapido y liviano sin necesidad de configurar bases de datos relacionales complejas.

---

## Estructura del Proyecto

TiendaTecnologica/
├── data/                            # Archivos CSV para persistencia de datos
│   ├── clientes.csv
│   ├── compras.csv
│   ├── detalle_compras.csv
│   ├── detalle_ventas.csv
│   ├── devoluciones.csv
│   ├── garantias.csv
│   ├── movimientos.csv
│   ├── productos.csv
│   ├── proveedores.csv
│   └── ventas.csv
├── src/
│   └── main/
│       ├── java/com/tiendatecnologica/
│       │   ├── controller/          # Controladores JavaFX (Interfaz grafica)
│       │   │   ├── ClientesController.java
│       │   │   ├── ComprasController.java
│       │   │   ├── DevolucionesController.java
│       │   │   ├── GarantiasController.java
│       │   │   ├── InventarioController.java
│       │   │   ├── MainController.java
│       │   │   ├── ProductosController.java
│       │   │   ├── ProveedoresController.java
│       │   │   ├── ReportesController.java
│       │   │   └── VentasController.java
│       │   ├── enums/               # Enumeraciones del sistema
│       │   │   ├── CategoriaProducto.java
│       │   │   ├── EstadoGarantia.java
│       │   │   ├── EstadoVenta.java
│       │   │   ├── MetodoPago.java
│       │   │   └── TipoMovimiento.java
│       │   ├── model/               # Entidades y modelos de dominio
│       │   │   ├── Cliente.java
│       │   │   ├── Compra.java
│       │   │   ├── DetalleCompra.java
│       │   │   ├── DetalleVenta.java
│       │   │   ├── Devolucion.java
│       │   │   ├── Garantia.java
│       │   │   ├── MovimientoInventario.java
│       │   │   ├── Producto.java
│       │   │   ├── Proveedor.java
│       │   │   └── Venta.java
│       │   ├── persistence/         # Lectores, escritores y parseo de archivos CSV
│       │   │   ├── CsvReader.java
│       │   │   └── CsvWriter.java
│       │   ├── repository/          # Acceso a datos (Patron Repository)
│       │   │   ├── ClienteRepository.java
│       │   │   ├── CompraRepository.java
│       │   │   ├── DevolucionRepository.java
│       │   │   ├── GarantiaRepository.java
│       │   │   ├── MovimientoRepository.java
│       │   │   ├── ProductoRepository.java
│       │   │   ├── ProveedorRepository.java
│       │   │   └── VentaRepository.java
│       │   ├── service/             # Capa de logica de negocio
│       │   │   ├── ClienteService.java
│       │   │   ├── CompraService.java
│       │   │   ├── DevolucionService.java
│       │   │   ├── GarantiaService.java
│       │   │   ├── InventarioService.java
│       │   │   ├── ProductoService.java
│       │   │   ├── ProveedorService.java
│       │   │   ├── ReporteService.java
│       │   │   └── VentaService.java
│       │   ├── util/                # Clases auxiliares y validaciones
│       │   │   ├── IdGenerator.java
│       │   │   ├── ValidationUtils.java
│       │   │   └── ViewSwitcher.java
│       │   └── Main.java            # Clase principal / Punto de entrada
│       └── resources/
│           ├── css/                 # Hojas de estilo
│           │   └── styles.css
│           └── fxml/                # Vistas en XML para JavaFX
│               ├── clientes.fxml
│               ├── compras.fxml
│               ├── devoluciones.fxml
│               ├── garantias.fxml
│               ├── inventario.fxml
│               ├── main.fxml
│               ├── productos.fxml
│               ├── proveedores.fxml
│               ├── reportes.fxml
│               └── ventas.fxml
├── pom.xml                          # Archivo de configuracion Maven
└── README.md

---

## Tecnologias Utilizadas

- Lenguaje: Java 17 (o superior)
- Interfaz Grafica: JavaFX (FXML y CSS)
- Gestor de Dependencias y Construccion: Apache Maven
- Persistencia: Archivos de texto plano (.csv)

---

## Requisitos Previos

- JDK 17 o superior instalado y configurado en el sistema.
- Maven instalado (o hacer uso del wrapper incluido ./mvnw).
- Opcional: Scene Builder para la edicion grafica de las vistas .fxml.

---

## Instalacion y Ejecucion

1. Clonar el repositorio:
   git clone https://github.com/ElAkemi/TiendaTecnologica.git
   cd TiendaTecnologica

2. Compilar el proyecto con Maven:
   ./mvnw clean compile

3. Ejecutar la aplicacion:
   ./mvnw javafx:run

---

## Modelo de Datos (Archivos CSV)

Los datos del sistema se gestionan automaticamente en el directorio data/:
- productos.csv y proveedores.csv: Catalogo base.
- ventas.csv y detalle_ventas.csv: Registro de ventas e items vendidos.
- compras.csv y detalle_compras.csv: Registro de ordenes de compra.
- movimientos.csv: Historico de cambios en el inventario.
- garantias.csv y devoluciones.csv: Seguimiento posventa.
