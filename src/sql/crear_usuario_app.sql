-- Usuario propio para la aplicación, con permisos solo sobre esta base.
-- Ejecutar como root en MySQL Workbench. Cambie CAMBIAR_CLAVE antes de correrlo
-- y NO suba al repositorio una versión con la clave real.
CREATE USER IF NOT EXISTS 'tienda_app'@'localhost' IDENTIFIED BY 'CAMBIAR_CLAVE';
GRANT SELECT, INSERT, UPDATE, DELETE ON tienda_tecnologica.* TO 'tienda_app'@'localhost';
FLUSH PRIVILEGES;