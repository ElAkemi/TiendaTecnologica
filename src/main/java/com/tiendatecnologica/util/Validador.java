package com.tiendatecnologica.util;

import java.util.regex.Pattern;

public final class Validador {

    private static final Pattern PATRON_EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private Validador() {
    }

    public static void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
    }

    public static void validarPositivo(double valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor que 0");
        }
    }

    public static void validarNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo");
        }
    }

    public static void validarEmail(String correo) {
        if (correo == null || !PATRON_EMAIL.matcher(correo).matches()) {
            throw new IllegalArgumentException("Correo inválido, debe de contener texto o numeros y @gmail.com: " + correo);
        }
    }

    public static void validarTelefono(String telefono) {
        if (telefono == null || !telefono.matches("\\d{8}")) {
            throw new IllegalArgumentException("Teléfono inválido debe de tener 8 digitos: " + telefono);
        }
    }

    public static void validarNombre(String nombre) {
        validarTexto(nombre, "Nombre");
        if (!nombre.trim().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
            throw new IllegalArgumentException("El nombre solo debe contener letras (sin números ni símbolos): " + nombre);
        }
    }

    public static void validarCedula(String cedula) {
        validarTexto(cedula, "Cédula");

        // Solo permite letras y números (alfanumérico), bloquea caracteres especiales y símbolos
        if (!cedula.trim().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("La cédula solo debe contener números (o letras si posee su cedula), sin caracteres especiales");
        }
    }
}
