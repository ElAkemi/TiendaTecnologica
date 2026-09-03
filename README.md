# TiendaTecnologia

Proyecto de Programación 3 — sistema de tienda de tecnología en Java + JavaFX
con persistencia en CSV. Arquitectura: `Model → Repository → Service → Controller → View`.

## Requisitos

- JDK 17 o superior
- Maven 3.8+
- (No requiere instalar JavaFX aparte: se descarga como dependencia)

## Cómo correr el proyecto

```bash
mvn clean javafx:run
```

Si prefieren usar el botón "Run" del IDE (IntelliJ/Eclipse/VS Code), deben
tener el plugin/soporte de Maven activado en el proyecto — al abrirlo como
proyecto Maven, el IDE ya reconoce las dependencias de JavaFX.

## Estructura

```
src/main/java/com/tiendatecnologia/
├── Main.java
├── model/          # Entidades (sin lógica de persistencia)
├── enums/          # Estados y tipos
├── repository/     # Únicos que conocen la estructura de los CSV
├── service/        # Lógica de negocio y validaciones
├── persistence/     # CsvReader / CsvWriter (genéricos, ya implementados)
├── controller/      # Controladores de JavaFX (sin lógica de negocio)
└── util/            # Validador, GeneradorId, Constantes (ya implementados)

src/main/resources/
├── fxml/
└── css/

data/                # Archivos CSV (ya vienen con encabezado)
```

## Regla de oro

Toda modificación de inventario pasa por `InventarioService`
(`registrarEntrada` / `registrarSalida` / `ajustarInventario`).
Nadie modifica `producto.setCantidadDisponible(...)` directamente
desde Venta ni desde Compra.

## Flujo de trabajo en Git

1. `main` contiene solo lo que los 4 construyeron juntos (esta base).
2. Cada quien crea su rama:
    - `feature/productos-inventario`
    - `feature/proveedores-compras`
    - `feature/ventas-facturacion`
    - `feature/garantias-reportes`
3. Dentro de cada rama: primero Model → Repository → Service (probarlo),
   después Controller → FXML.
4. Al integrar a `main`, avisar al resto antes de tocar clases que no son
   de su módulo (enums, util, persistence, Main, main.fxml).

## División de módulos

| Persona | Módulo | Paquetes / CSV |
|---|---|---|
| 1 | Productos e Inventario | `Producto`, `MovimientoInventario`, `productos.csv`, `movimientos.csv` |
| 2 | Proveedores y Compras | `Proveedor`, `OrdenCompra`, `DetalleCompra`, `proveedores.csv`, `compras.csv`, `detalle_compras.csv` |
| 3 | Ventas y Pagos | `Venta`, `DetalleVenta`, `Pago`, `Cliente`, `clientes.csv`, `ventas.csv`, `detalle_ventas.csv` |
| 4 | Garantías, Devoluciones y Reportes | `Garantia`, `Devolucion`, `garantias.csv`, `devoluciones.csv` |

## Pendiente por implementar (a propósito, no viene en esta base)

- Todas las clases `model/`, `repository/`, `service/` específicas de cada módulo.
- Los FXML de cada pantalla y sus controllers.
- La navegación real dentro de `MainController` (actualmente son `TODO`).
