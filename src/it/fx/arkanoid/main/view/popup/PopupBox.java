package it.fx.arkanoid.main.view.popup;

import it.fx.arkanoid.main.state.GameState;
import it.fx.arkanoid.main.utils.Constant;
import it.fx.arkanoid.main.utils.FileLoader;
import it.fx.arkanoid.main.view.Window;
import it.fx.arkanoid.main.view.scenes.GameScene;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

import static it.fx.arkanoid.main.utils.FileLoader.btnSprite;

public final class PopupBox {

    public static void showPauseMenu() {
        // Create the content
        Pane content = new Pane();
        VBox box = new VBox(50);
        ImageView btn_continue = new ImageView(btnSprite.get("Continue")[0]);
        ImageView btn_menu = new ImageView(btnSprite.get("Back to Menu")[0]);

        btn_continue.setOnMouseEntered(e -> btn_continue.setImage(btnSprite.get("Continue")[1]));
        btn_continue.setOnMouseExited(e -> btn_continue.setImage(btnSprite.get("Continue")[0]));

        btn_menu.setOnMouseEntered(e -> btn_menu.setImage(btnSprite.get("Back to Menu")[1]));
        btn_menu.setOnMouseExited(e -> btn_menu.setImage(btnSprite.get("Back to Menu")[0]));

        btn_continue.setOnMouseClicked(e -> {
            GameScene.getList().remove(content);
            GameState.setCurrentState(GameState.STATES.RUN_GAME);
        });

        btn_menu.setOnMouseClicked(e -> {
            GameScene.getList().remove(content);
            GameState.setCurrentState(GameState.STATES.NONE);
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
        content.relocate(Constant.W_WINDOW / 2.d - 100, Constant.H_WINDOW / 2.d - 100);
        content.setPrefSize(200, 200);

        GameScene.getList().add(content);
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
            FileLoader.saveScore(response, score);
            Window.setCurrentScene(Window.MENU);
        });
    }

    @SuppressWarnings("unchecked")
    public static void showScoreList() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Scores");
        dialog.getDialogPane().setPrefSize(300, 400);

        double w_cell = dialog.getDialogPane().getWidth() / 4;

        BorderPane table = new BorderPane();
        GridPane tableContent = new GridPane();
        HBox title = new HBox(30);

        Label lblName = new Label("Player Name");
        Label lblScore = new Label("Score");

        lblName.setPrefSize(w_cell, 50);
        lblScore.setPrefSize(w_cell, 50);

        title.getChildren().addAll(lblName, lblScore);
        tableContent.addRow(0, title);

        try(InputStream in = Files.newInputStream(FileLoader.scorePath)) {
            JSONObject jPlayers = (JSONObject) new JSONParser().parse(new InputStreamReader(in));
            JSONArray jArray = (JSONArray) jPlayers.get("player");

            jArray.sort((o1, o2) -> {
               Integer n1 = ((Number) ((JSONObject) o1).get("score")).intValue();
               Integer n2 = ((Number) ((JSONObject) o2).get("score")).intValue();

               return n2.compareTo(n1);
            });

            for (int i = 0; i < jArray.size(); i++) {
                JSONObject jPlayer = (JSONObject) jArray.get(i);
                Label namePlayer = new Label(jPlayer.get("name").toString());
                Label scoreLabel = new Label("" + jPlayer.get("score").toString());

                HBox row = new HBox(30);

                namePlayer.setPrefSize(w_cell, 50);
                scoreLabel.setPrefSize(w_cell, 50);

                row.getChildren().addAll(namePlayer, scoreLabel);
                row.setAlignment(Pos.CENTER);
                tableContent.addRow(i + 1, row);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        tableContent.setAlignment(Pos.TOP_CENTER);
        table.setCenter(tableContent);
        dialog.getDialogPane().setContent(table);
        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> dialog.close());
        dialog.show();
    }
}
