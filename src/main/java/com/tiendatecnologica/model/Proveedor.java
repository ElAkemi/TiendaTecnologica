package com.tiendatecnologica.model;

/**
 * Representa un proveedor de productos de la tienda.
 *
 * Contiene la información básica necesaria para identificar
 * y contactar a un proveedor.
 */
public class Proveedor {

    /** Identificador único del proveedor. */
    private String id;

    /** Nombre del proveedor o empresa. */
    private String nombre;

    /** Nombre de la persona de contacto del proveedor. */
    private String contacto;

    /** Número telefónico del proveedor. */
    private String telefono;

    /** Correo electrónico del proveedor. */
    private String correo;

    /**
     * Crea un nuevo proveedor con sus datos básicos.
     *
     * @param id identificador del proveedor
     * @param nombre nombre del proveedor o empresa
     * @param contacto persona de contacto
     * @param telefono número telefónico
     * @param correo correo electrónico
     */
    public Proveedor(String id, String nombre, String contacto,
                     String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
    }

    /**
     * Obtiene el identificador del proveedor.
     *
     * @return identificador del proveedor
     */
    public String getId() {
        return id;
    }

    /**
     * Modifica el identificador del proveedor.
     *
     * @param id nuevo identificador
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del proveedor.
     *
     * @return nombre del proveedor
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el nombre del proveedor.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el contacto del proveedor.
     *
     * @return persona de contacto
     */
    public String getContacto() {
        return contacto;
    }

    /**
     * Modifica la persona de contacto.
     *
     * @param contacto nuevo contacto
     */
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /**
     * Obtiene el número telefónico del proveedor.
     *
     * @return número telefónico
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Modifica el número telefónico del proveedor.
     *
     * @param telefono nuevo número telefónico
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el correo electrónico del proveedor.
     *
     * @return correo electrónico
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Modifica el correo electrónico del proveedor.
     *
     * @param correo nuevo correo electrónico
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }
}