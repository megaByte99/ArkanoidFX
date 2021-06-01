package it.fx.arkanoid;

import it.fx.arkanoid.main.view.Window;
import it.fx.arkanoid.main.utils.FileLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ArkanoidFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /*
         *  TODO Ordine di esecuzione:
         *      - FileLoader
         *      - Textures
         *      - Gioco
         */

        if (FileLoader.loadResource()) {
            Window.init(stage);
        } else {
            Alert errorBox = new Alert(Alert.AlertType.ERROR, "Error Load Textures or JSON Data", ButtonType.OK);
            errorBox.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK)
                    Platform.exit();
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}

