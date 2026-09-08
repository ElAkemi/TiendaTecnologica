package com.tiendatecnologica.service;

import com.tiendatecnologica.model.Cliente;
import com.tiendatecnologica.repository.ClienteRepository;
//se importa validador y generador de ids
import com.tiendatecnologica.util.GeneradorId;
import com.tiendatecnologica.util.Validador;

import java.util.List;

/**
 * Lógica de negocio y validaciones sobre clientes. Los Controllers hablan
 * con esta clase, nunca directamente con ClienteRepository.
 */
public class ClienteService {
    //se necesita crear un atributo repository para que este guarde los cambios en el csv ya que
    //repository no conoce como crear,modoifcar etc pero service si
    private final ClienteRepository clienteRepository = new ClienteRepository();

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

        Cliente cliente = new Cliente(GeneradorId.generarIdCliente(), cedula, nombre, telefono, email);
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
