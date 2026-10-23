-- =====================================================================
-- schema.sql - Tienda Tecnológica (EIF206 - Proyecto 2)
-- Esquema relacional normalizado hasta 3FN.
-- Se basa en las clases model/ y en los encabezados de los CSV del P1.
-- Ejecutar completo (rayo) en MySQL Workbench. Es re-ejecutable.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS tienda_tecnologica
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tienda_tecnologica;

-- Borrado en orden inverso a las dependencias (permite re-ejecutar).
DROP TABLE IF EXISTS devolucion;
DROP TABLE IF EXISTS garantia;
DROP TABLE IF EXISTS movimiento_inventario;
DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS detalle_compra;
DROP TABLE IF EXISTS orden_compra;
DROP TABLE IF EXISTS producto_proveedor;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS proveedor;
DROP TABLE IF EXISTS producto;

-- ---------------------------------------------------------------------
-- Catálogo
-- ---------------------------------------------------------------------
CREATE TABLE producto (
                          codigo              VARCHAR(30)   NOT NULL,
                          nombre              VARCHAR(100)  NOT NULL,
                          categoria           VARCHAR(50)   NOT NULL,
                          marca               VARCHAR(50)   NOT NULL,
                          precio_compra       DECIMAL(12,2) NOT NULL CHECK (precio_compra >= 0),
                          precio_venta        DECIMAL(12,2) NOT NULL CHECK (precio_venta >= 0),
                          cantidad_disponible INT           NOT NULL DEFAULT 0 CHECK (cantidad_disponible >= 0),
                          stock_minimo        INT           NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),
                          meses_garantia      INT           NOT NULL DEFAULT 0 CHECK (meses_garantia >= 0),
                          PRIMARY KEY (codigo)
) ENGINE=InnoDB;

CREATE TABLE proveedor (
                           id        VARCHAR(20)  NOT NULL,          -- formato PROV-00001, lo genera el sistema
                           nombre    VARCHAR(100) NOT NULL,
                           contacto  VARCHAR(100),
                           telefono  VARCHAR(20),
                           correo    VARCHAR(100),
                           PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Relación N:M: un producto puede tener varios proveedores y viceversa.
CREATE TABLE producto_proveedor (
                                    codigo_producto VARCHAR(30) NOT NULL,
                                    id_proveedor    VARCHAR(20) NOT NULL,
                                    PRIMARY KEY (codigo_producto, id_proveedor),
                                    CONSTRAINT fk_pp_producto  FOREIGN KEY (codigo_producto)
                                        REFERENCES producto (codigo)  ON UPDATE CASCADE ON DELETE CASCADE,
                                    CONSTRAINT fk_pp_proveedor FOREIGN KEY (id_proveedor)
                                        REFERENCES proveedor (id)     ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cliente (
                         id        VARCHAR(20)  NOT NULL,          -- formato CLI-00001, lo genera el sistema
                         cedula    VARCHAR(20)  NOT NULL,
                         nombre    VARCHAR(100) NOT NULL,
                         telefono  VARCHAR(20),
                         email     VARCHAR(100),
                         PRIMARY KEY (id),
                         UNIQUE KEY uq_cliente_cedula (cedula)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Ventas
-- El pago (método, monto recibido, vuelto) es 1:1 con la venta, por eso
-- va dentro de la misma tabla. Subtotal, impuesto y total se guardan
-- porque son un registro histórico (el IVA puede cambiar con el tiempo).
-- ---------------------------------------------------------------------
CREATE TABLE venta (
                       id             VARCHAR(20)   NOT NULL,    -- formato V-00001
                       fecha          DATETIME      NOT NULL,
                       id_cliente     VARCHAR(20),               -- NULL = venta sin cliente registrado
                       subtotal       DECIMAL(12,2) NOT NULL,
                       impuesto       DECIMAL(12,2) NOT NULL,
                       total          DECIMAL(12,2) NOT NULL,
                       metodo_pago    ENUM('EFECTIVO','TARJETA','SINPE') NOT NULL,
                       monto_recibido DECIMAL(12,2) NOT NULL,
                       vuelto         DECIMAL(12,2) NOT NULL DEFAULT 0,
                       estado         ENUM('COMPLETADA','ANULADA') NOT NULL DEFAULT 'COMPLETADA',
                       PRIMARY KEY (id),
                       CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente)
                           REFERENCES cliente (id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
                               id              INT           NOT NULL AUTO_INCREMENT,
                               id_venta        VARCHAR(20)   NOT NULL,
                               codigo_producto VARCHAR(30)   NOT NULL,
                               cantidad        INT           NOT NULL CHECK (cantidad > 0),
                               precio_unitario DECIMAL(12,2) NOT NULL,   -- precio al momento de vender
                               PRIMARY KEY (id),
                               CONSTRAINT fk_dv_venta    FOREIGN KEY (id_venta)
                                   REFERENCES venta (id)       ON UPDATE CASCADE ON DELETE CASCADE,
                               CONSTRAINT fk_dv_producto FOREIGN KEY (codigo_producto)
                                   REFERENCES producto (codigo) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Compras a proveedores
-- ---------------------------------------------------------------------
CREATE TABLE orden_compra (
                              id_orden      VARCHAR(20) NOT NULL,       -- formato C-00001
                              id_proveedor  VARCHAR(20) NOT NULL,
                              fecha         DATE        NOT NULL,
                              estado        ENUM('PENDIENTE','RECIBIDA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
                              PRIMARY KEY (id_orden),
                              CONSTRAINT fk_oc_proveedor FOREIGN KEY (id_proveedor)
                                  REFERENCES proveedor (id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalle_compra (
                                id              INT           NOT NULL AUTO_INCREMENT,
                                id_orden        VARCHAR(20)   NOT NULL,
                                codigo_producto VARCHAR(30)   NOT NULL,
                                cantidad        INT           NOT NULL CHECK (cantidad > 0),
                                costo_unitario  DECIMAL(12,2) NOT NULL,
                                PRIMARY KEY (id),
                                CONSTRAINT fk_dc_orden    FOREIGN KEY (id_orden)
                                    REFERENCES orden_compra (id_orden) ON UPDATE CASCADE ON DELETE CASCADE,
                                CONSTRAINT fk_dc_producto FOREIGN KEY (codigo_producto)
                                    REFERENCES producto (codigo)       ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Inventario
-- ---------------------------------------------------------------------
CREATE TABLE movimiento_inventario (
                                       id                VARCHAR(20)  NOT NULL,  -- formato MOV-00001
                                       codigo_producto   VARCHAR(30)  NOT NULL,
                                       tipo              ENUM('ENTRADA','SALIDA','AJUSTE') NOT NULL,
                                       cantidad          INT          NOT NULL,
                                       cantidad_anterior INT          NOT NULL,
                                       cantidad_nueva    INT          NOT NULL,
                                       fecha             DATETIME     NOT NULL,
                                       motivo            VARCHAR(255),
                                       PRIMARY KEY (id),
                                       CONSTRAINT fk_mov_producto FOREIGN KEY (codigo_producto)
                                           REFERENCES producto (codigo) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Posventa
-- ---------------------------------------------------------------------
CREATE TABLE garantia (
                          id                   VARCHAR(20) NOT NULL, -- formato GAR-00001
                          id_venta             VARCHAR(20) NOT NULL,
                          codigo_producto      VARCHAR(30) NOT NULL,
                          fecha_solicitud      DATETIME    NOT NULL,
                          descripcion_problema TEXT        NOT NULL,
                          estado ENUM('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA','FINALIZADA')
           NOT NULL DEFAULT 'PENDIENTE',
                          PRIMARY KEY (id),
                          CONSTRAINT fk_gar_venta    FOREIGN KEY (id_venta)
                              REFERENCES venta (id)        ON UPDATE CASCADE ON DELETE RESTRICT,
                          CONSTRAINT fk_gar_producto FOREIGN KEY (codigo_producto)
                              REFERENCES producto (codigo) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE devolucion (
                            id               VARCHAR(20)  NOT NULL,   -- formato DEV-00001
                            id_venta         VARCHAR(20)  NOT NULL,
                            codigo_producto  VARCHAR(30)  NOT NULL,
                            cantidad         INT          NOT NULL CHECK (cantidad > 0),
                            fecha_solicitud  DATETIME     NOT NULL,
                            motivo           VARCHAR(255) NOT NULL,
                            estado ENUM('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA','FINALIZADA')
           NOT NULL DEFAULT 'PENDIENTE',
                            PRIMARY KEY (id),
                            CONSTRAINT fk_dev_venta    FOREIGN KEY (id_venta)
                                REFERENCES venta (id)        ON UPDATE CASCADE ON DELETE RESTRICT,
                            CONSTRAINT fk_dev_producto FOREIGN KEY (codigo_producto)
                                REFERENCES producto (codigo) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Índices para las consultas de reportes (ventas por fecha, movimientos por producto).
CREATE INDEX idx_venta_fecha      ON venta (fecha);
CREATE INDEX idx_mov_producto     ON movimiento_inventario (codigo_producto, fecha);