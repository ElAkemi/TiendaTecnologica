package com.tiendatecnologica.service;

import com.tiendatecnologica.model.Cliente;
// CAMBIO SQL: se importa el repositorio JDBC (MySQL) en lugar del que usa CSV.
import com.tiendatecnologica.repository.ClienteJDBC_Repository;
// CAMBIO ID: se quitó el import de GeneradorId. Ya no se usa, porque el id
// ahora lo genera la base de datos con siguienteId().
import com.tiendatecnologica.util.Validador;

import java.util.List;

/**
 * Lógica de negocio y validaciones sobre clientes. Los Controllers hablan
 * con esta clase, nunca directamente con el repositorio.
 */
public class ClienteService {

    // CAMBIO SQL: ahora el service guarda y lee los clientes en MySQL.
    // El nombre de la variable se mantiene para no tocar el resto de la clase.
    private final ClienteJDBC_Repository clienteRepository = new ClienteJDBC_Repository();

    // CAMBIO ID: prefijo con el que se generan los ids (CLI-00001, CLI-00002...).
    private static final String PREFIJO_ID = "CLI-";

    /**
     * Registra un nuevo cliente, validando sus datos y que la cédula no
     * esté ya registrada. El id se genera automáticamente.
     *
     * @param cedula cédula del cliente
     * @param nombre nombre completo del cliente
     * @param telefono teléfono de contacto (8 dígitos)
     * @param email correo electrónico del cliente
     * @return el cliente ya registrado, con su id asignado
     * @throws IllegalArgumentException si algún dato es inválido o la cédula ya existe
     */
    public Cliente registrarCliente(String cedula, String nombre, String telefono, String email) {
        Validador.validarTexto(cedula, "Cédula");
        Validador.validarCedula(cedula);
        Validador.validarNombre(nombre);
        Validador.validarTexto(nombre, "Nombre");
        Validador.validarTelefono(telefono);
        Validador.validarEmail(email);

        // Sin cambios de lógica: buscarPorCedula ahora es un SELECT con WHERE en MySQL
        // (antes recorría el CSV completo).
        if (clienteRepository.buscarPorCedula(cedula) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con la cédula " + cedula);
        }
        for (Cliente c : clienteRepository.obtenerTodos()) {
            if (c.getTelefono() != null && c.getTelefono().equals(telefono)) {
                throw new IllegalArgumentException("Ya existe un cliente con el teléfono " + telefono);
            }
            if (c.getEmail() != null && c.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("Ya existe un cliente con el correo " + email);
            }
        }

        // CAMBIO ID: antes se usaba GeneradorId.generarIdCliente(), que siempre
        // devolvía CLI-00001. Ahora siguienteId() consulta el mayor número usado
        // en la tabla cliente y devuelve el siguiente con formato CLI-00001.
        Cliente cliente = new Cliente(clienteRepository.siguienteId(PREFIJO_ID),
                cedula, nombre, telefono, email);
        clienteRepository.guardar(cliente);
        return cliente;
    }

    /**
     * Modifica un cliente existente.
     *
     * @param cliente cliente con los datos actualizados (identificado por su id)
     * @throws IllegalArgumentException si algún dato es inválido o el id no existe
     */
    public void modificarCliente(Cliente cliente) {
        Validador.validarTexto(cliente.getCedula(), "Cédula");
        Validador.validarTexto(cliente.getNombre(), "Nombre");
        Validador.validarCedula(cliente.getCedula());
        Validador.validarNombre(cliente.getNombre());
        Validador.validarTelefono(cliente.getTelefono());
        Validador.validarEmail(cliente.getEmail());

        if (!clienteRepository.existe(cliente.getId())) {
            throw new IllegalArgumentException("No existe un cliente con el id " + cliente.getId());
        }
        // CAMBIO SQL: si la cédula nueva ya la tiene otro cliente, MySQL lo rechaza
        // (restricción UNIQUE) y RepositorioException muestra el mensaje.
        clienteRepository.actualizar(cliente);
    }

    /**
     * Busca un cliente por su id.
     *
     * @param id id a buscar
     * @return el cliente encontrado, o null si no existe
     */
    public Cliente buscarPorId(String id) {
        return clienteRepository.buscarPorClave(id);
    }

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula cédula a buscar
     * @return el cliente encontrado, o null si no existe
     */
    public Cliente buscarPorCedula(String cedula) {
        return clienteRepository.buscarPorCedula(cedula);
    }

    /**
     * @return todos los clientes registrados
     */
    public List<Cliente> listarClientes() {
        return clienteRepository.obtenerTodos();
    }
}