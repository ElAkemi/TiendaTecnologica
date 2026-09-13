package com.tiendatecnologica.util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria encargada de realizar validaciones comunes
 * utilizadas en diferentes partes del sistema.
 *
 * Incluye validaciones para textos obligatorios, valores positivos,
 * valores no negativos, correos electrónicos, teléfonos, nombres
 * y cédulas.
 */
public final class Validador {

    /**
     * Patrón utilizado para validar el formato de un correo electrónico.
     */
    private static final Pattern PATRON_EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    /**
     * Constructor privado para evitar que la clase sea instanciada.
     *
     * Esta clase solamente contiene métodos estáticos de utilidad.
     */
    private Validador() {
    }

    /**
     * Valida que un texto no sea nulo ni esté vacío.
     *
     * @param valor texto que se desea validar
     * @param campo nombre del campo que se está validando
     * @throws IllegalArgumentException si el texto es nulo o está vacío
     */
    public static void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }
    }

    /**
     * Valida que un valor numérico sea mayor que cero.
     *
     * @param valor valor que se desea validar
     * @param campo nombre del campo que se está validando
     * @throws IllegalArgumentException si el valor es menor o igual a cero
     */
    public static void validarPositivo(double valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(
                    campo + " debe ser mayor que 0"
            );
        }
    }

    /**
     * Valida que un valor entero no sea negativo.
     *
     * @param valor valor que se desea validar
     * @param campo nombre del campo que se está validando
     * @throws IllegalArgumentException si el valor es menor que cero
     */
    public static void validarNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(
                    campo + " no puede ser negativo"
            );
        }
    }

    /**
     * Valida que un correo electrónico cumpla con el formato establecido
     * por el patrón definido en {@link #PATRON_EMAIL}.
     *
     * @param correo correo electrónico que se desea validar
     * @throws IllegalArgumentException si el correo es nulo o no cumple
     *         con el formato esperado
     */
    public static void validarEmail(String correo) {
        if (correo == null
                || !PATRON_EMAIL.matcher(correo).matches()) {

            throw new IllegalArgumentException(
                    "Correo inválido, debe de contener texto o numeros "
                            + "y @gmail.com: " + correo
            );
        }
    }

    /**
     * Valida que un teléfono contenga exactamente ocho dígitos.
     *
     * @param telefono número telefónico que se desea validar
     * @throws IllegalArgumentException si el teléfono es nulo o no contiene
     *         exactamente ocho dígitos
     */
    public static void validarTelefono(String telefono) {
        if (telefono == null || !telefono.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "Teléfono inválido debe de tener 8 digitos: "
                            + telefono
            );
        }
    }

    /**
     * Valida que un nombre solamente contenga letras y espacios.
     *
     * También permite letras con tildes, la letra ñ y la diéresis.
     *
     * @param nombre nombre que se desea validar
     * @throws IllegalArgumentException si el nombre está vacío o contiene
     *         números o caracteres especiales
     */
    public static void validarNombre(String nombre) {
        validarTexto(nombre, "Nombre");

        if (!nombre.trim().matches(
                "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$"
        )) {
            throw new IllegalArgumentException(
                    "El nombre solo debe contener letras "
                            + "(sin números ni símbolos): " + nombre
            );
        }
    }

    /**
     * Valida que una cédula solamente contenga caracteres alfanuméricos.
     *
     * Esto permite números y letras, pero bloquea caracteres especiales
     * y símbolos.
     *
     * @param cedula cédula que se desea validar
     * @throws IllegalArgumentException si la cédula está vacía o contiene
     *         caracteres especiales
     */
    public static void validarCedula(String cedula) {
        validarTexto(cedula, "Cédula");

        // Solo permite letras y números y bloquea caracteres especiales.
        if (!cedula.trim().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException(
                    "La cédula solo debe contener números "
                            + "(o letras si posee su cedula), "
                            + "sin caracteres especiales"
            );
        }
    }
}