package com.tiendatecnologica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada principal de la aplicación.
 *
 * Se encarga de iniciar JavaFX, cargar la vista principal de la aplicación
 * y configurar la ventana inicial.
 *
 * Esta clase no contiene lógica de negocio, ya que dicha lógica pertenece
 * a las capas correspondientes del sistema.
 */
public class Main extends Application {

    /**
     * Inicia la aplicación JavaFX.
     *
     * Carga la vista principal desde su archivo FXML, configura la escena,
     * aplica la hoja de estilos y muestra la ventana principal.
     *
     * @param stage ventana principal proporcionada por JavaFX
     * @throws IOException si no es posible cargar la vista o la hoja de estilos
     */
    @Override
    public void start(Stage stage) throws IOException {
        /*
         * Carga la vista principal desde el archivo FXML.
         */
        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/main.fxml")
        );

        /*
         * Crea la escena utilizando la vista principal
         * y establece sus dimensiones iniciales.
         */
        Scene scene = new Scene(root, 900, 600);

        /*
         * Carga y aplica la hoja de estilos CSS de la aplicación.
         */
        scene.getStylesheets().add(
                getClass()
                        .getResource("/css/styles.css")
                        .toExternalForm()
        );

        // Configura el título y la escena de la ventana principal.
        stage.setTitle("Tienda de Tecnología");
        stage.setScene(scene);

        // Muestra la ventana al usuario.
        stage.show();
    }

    /**
     * Método principal de la aplicación.
     *
     * Inicia el ciclo de vida de JavaFX mediante {@link #launch(String...)}.
     *
     * @param args argumentos recibidos desde la línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}