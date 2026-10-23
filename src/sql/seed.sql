-- =====================================================================
-- seed.sql - Datos de prueba (inventados) para Tienda Tecnológica.
-- Ejecutar DESPUÉS de schema.sql. Si ya tienen datos reales en los CSV
-- de data/, se pueden reemplazar estas filas por esos valores.
-- =====================================================================
USE tienda_tecnologica;

INSERT INTO producto (codigo, nombre, categoria, marca, precio_compra, precio_venta, cantidad_disponible, stock_minimo, meses_garantia) VALUES
                                                                                                                                            ('LAP-001', 'Laptop Pavilion 15',        'Computadoras', 'HP',       350000.00, 450000.00, 8,  3, 12),
                                                                                                                                            ('MOU-001', 'Mouse inalámbrico M185',    'Periféricos',  'Logitech',   6000.00,   9500.00, 40, 10, 6),
                                                                                                                                            ('TEC-001', 'Teclado mecánico K552',     'Periféricos',  'Redragon',  18000.00,  27000.00, 15, 5, 6),
                                                                                                                                            ('MON-001', 'Monitor 24" IPS',           'Monitores',    'Samsung',   85000.00, 115000.00, 2,  4, 24),
                                                                                                                                            ('AUD-001', 'Audífonos Bluetooth Tune',  'Audio',        'JBL',       22000.00,  32000.00, 20, 5, 12);

INSERT INTO proveedor (id, nombre, contacto, telefono, correo) VALUES
                                                                   ('PROV-00001', 'Distribuidora Tech CR',  'María Rojas',  '22001111', 'ventas@techcr.example'),
                                                                   ('PROV-00002', 'Importadora Digital',    'Luis Mora',    '22002222', 'contacto@impdigital.example');

INSERT INTO producto_proveedor (codigo_producto, id_proveedor) VALUES
                                                                   ('LAP-001', 'PROV-00001'),
                                                                   ('MOU-001', 'PROV-00001'),
                                                                   ('MOU-001', 'PROV-00002'),
                                                                   ('TEC-001', 'PROV-00002'),
                                                                   ('MON-001', 'PROV-00001'),
                                                                   ('AUD-001', 'PROV-00002');

INSERT INTO cliente (id, cedula, nombre, telefono, email) VALUES
                                                              ('CLI-00001', '101110111', 'Ana Vargas',   '88881111', 'ana@example.com'),
                                                              ('CLI-00002', '202220222', 'Pedro Solís',  '88882222', 'pedro@example.com');

-- Dos ventas completadas (subtotal + IVA 13% = total).
INSERT INTO venta (id, fecha, id_cliente, subtotal, impuesto, total, metodo_pago, monto_recibido, vuelto, estado) VALUES
                                                                                                                      ('V-00001', '2026-10-10 10:30:00', 'CLI-00001', 459500.00, 59735.00, 519235.00, 'TARJETA',  519235.00, 0.00,    'COMPLETADA'),
                                                                                                                      ('V-00002', '2026-10-12 15:10:00', 'CLI-00002',  27000.00,  3510.00,  30510.00, 'EFECTIVO',  35000.00, 4490.00, 'COMPLETADA');

INSERT INTO detalle_venta (id_venta, codigo_producto, cantidad, precio_unitario) VALUES
                                                                                     ('V-00001', 'LAP-001', 1, 450000.00),
                                                                                     ('V-00001', 'MOU-001', 1,   9500.00),
                                                                                     ('V-00002', 'TEC-001', 1,  27000.00);

INSERT INTO orden_compra (id_orden, id_proveedor, fecha, estado) VALUES
                                                                     ('C-00001', 'PROV-00001', '2026-10-01', 'RECIBIDA'),
                                                                     ('C-00002', 'PROV-00002', '2026-10-15', 'PENDIENTE');

INSERT INTO detalle_compra (id_orden, codigo_producto, cantidad, costo_unitario) VALUES
                                                                                     ('C-00001', 'LAP-001', 10, 350000.00),
                                                                                     ('C-00001', 'MON-001',  5,  85000.00),
                                                                                     ('C-00002', 'AUD-001', 10,  22000.00);

INSERT INTO movimiento_inventario (id, codigo_producto, tipo, cantidad, cantidad_anterior, cantidad_nueva, fecha, motivo) VALUES
                                                                                                                              ('MOV-00001', 'LAP-001', 'ENTRADA', 10,  0, 10, '2026-10-01 09:00:00', 'Recepción orden C-00001'),
                                                                                                                              ('MOV-00002', 'LAP-001', 'SALIDA',   1, 10,  9, '2026-10-10 10:30:00', 'Venta V-00001'),
                                                                                                                              ('MOV-00003', 'LAP-001', 'AJUSTE',   8,  9,  8, '2026-10-14 08:00:00', 'Ajuste por conteo físico');

INSERT INTO garantia (id, id_venta, codigo_producto, fecha_solicitud, descripcion_problema, estado) VALUES
    ('GAR-00001', 'V-00001', 'MOU-001', '2026-10-18 11:00:00', 'El mouse dejó de responder', 'EN_REVISION');

INSERT INTO devolucion (id, id_venta, codigo_producto, cantidad, fecha_solicitud, motivo, estado) VALUES
    ('DEV-00001', 'V-00002', 'TEC-001', 1, '2026-10-16 16:20:00', 'Teclas defectuosas', 'PENDIENTE');