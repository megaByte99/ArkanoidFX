package main.game.window.graphics;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;
import main.game.utils.gameState.GameState;
import main.game.window.GameScene;
import main.game.Window;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static main.game.utils.Constant.H_WINDOW;
import static main.game.utils.Constant.W_WINDOW;
import static main.game.utils.FileLoader.*;

public final class GraphicBox {

    public static void showPauseMenu() {
        // Create the content
        Pane content = new Pane();
        VBox box = new VBox(50);
        ImageView btn_continue = new ImageView(btn_sprite.get("Continue")[0]);
        ImageView btn_menu = new ImageView(btn_sprite.get("Back to Menu")[0]);

        btn_continue.setOnMouseEntered(e -> btn_continue.setImage(btn_sprite.get("Continue")[1]));
        btn_continue.setOnMouseExited(e -> btn_continue.setImage(btn_sprite.get("Continue")[0]));

        btn_menu.setOnMouseEntered(e -> btn_menu.setImage(btn_sprite.get("Back to Menu")[1]));
        btn_menu.setOnMouseExited(e -> btn_menu.setImage(btn_sprite.get("Back to Menu")[0]));

        btn_continue.setOnMouseClicked(e -> {
            GameScene.getBackgroundList().remove(content);
            GameState.setCurrentState(GameState.STATES.RUN_GAME);
        });

        btn_menu.setOnMouseClicked(e -> {
            GameScene.getBackgroundList().remove(content);
            Window.setCurrentScene(Window.MENU);
        });

        box.getChildren().addAll(btn_continue, btn_menu);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(150, 150);
        box.relocate(25, 25);

        content.getChildren().add(box);
        content.setStyle(
                "-fx-background-color: rgba(0.04, 0.02, 0.08, 0.5) "
        );
        content.relocate(W_WINDOW / 2.d - 100, H_WINDOW / 2.d - 100);
        content.setPrefSize(200, 200);

        GameScene.getBackgroundList().add(content);
    }

    public static void showSaveScoreBox(String result, int score) {
        TextInputDialog dialog = new TextInputDialog("name");
        dialog.setTitle("Save score");
        dialog.setHeaderText(((result.equals("Win")) ? "You Win" : "You Lose") + "\nYour score is: " + score);
        dialog.setContentText("Please enter your name:");

        // Close the window if i press cancel button
        Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancel.addEventFilter(ActionEvent.ACTION, e -> Window.setCurrentScene(Window.MENU));

        dialog.showAndWait().ifPresent(response -> {
            saveScore(response, score);
            Window.setCurrentScene(Window.MENU);
        });
    }

    public static void showScoreBox() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Scores");
        dialog.getDialogPane().setPrefSize(300, 400);

        double w_cell = dialog.getDialogPane().getWidth() / 2;

        // Create a table using a GridPane
        GridPane table = new GridPane();
        table.setAlignment(Pos.TOP_CENTER);

        // Create the first row which represent the title of the columns
        HBox title = new HBox();

        Label t_namePlayer = new Label("Player"),
                t_scoreLabel = new Label("Score");

        t_namePlayer.setPrefSize(w_cell, 50);
        t_scoreLabel.setPrefSize(w_cell, 50);

        t_namePlayer.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        t_scoreLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 20));

        t_namePlayer.setStyle("-fx-background-color: #6ABEE6");
        t_scoreLabel.setStyle("-fx-background-color: #6ABEE6");

        t_namePlayer.setPadding(new Insets(10, 20, 10, 20));
        t_scoreLabel.setPadding(new Insets(10, 20, 10, 20));

        title.getChildren().addAll(t_namePlayer, t_scoreLabel);
        table.addRow(0, title);

        // Read the json data
        try {
            Path scorePath = Path.of(jarPath + "scores/score.json");
            if (Files.exists(scorePath)) {
                JSONObject obj = (JSONObject) new JSONParser().parse(new InputStreamReader(Files.newInputStream(scorePath)));
                JSONArray array = (JSONArray) obj.get("player");

                List<Pair<String, Integer>> data = new ArrayList<>();
                for (Object o : array) {
                    String player = (String)((JSONObject) o).get("name");
                    Integer score =  ((Long)((JSONObject) o).get("score")).intValue();
                    data.add(new Pair<>(player, score));
                }

                // Add rows to table
                for (int i = 0; i < data.size(); i++) {
                    Pair<String, Integer> rowData = data.get(i);

                    Label namePlayer = new Label(rowData.getKey());
                    Label scoreLabel = new Label("" + rowData.getValue());

                    namePlayer.setPrefSize(w_cell, 25);
                    scoreLabel.setPrefSize(w_cell, 25);

                    namePlayer.setFont(Font.font("Roboto", FontWeight.NORMAL, 12));
                    scoreLabel.setFont(Font.font("Roboto", FontWeight.NORMAL, 12));

                    namePlayer.setPadding(new Insets(5, 20, 5, 30));
                    scoreLabel.setPadding(new Insets(5, 20, 5, 30));

                    HBox row = new HBox();

                    row.getChildren().addAll(namePlayer, scoreLabel);
                    table.addRow(i + 1, row);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().setContent(table);
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> dialog.close());
        dialog.show();
    }
}
