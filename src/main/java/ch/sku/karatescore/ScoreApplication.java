package ch.sku.karatescore;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class ScoreApplication extends Application {    @Override
public void start(Stage primaryStage) {
    primaryStage.setTitle("Karate Scoreboard");

    primaryStage.setTitle("Karate Scoreboard");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20, 50, 20, 50)); // Adjust padding as needed

    // Initialize components for Aka and Ao
    ScoreComponent akaComponent = new ScoreComponent("Aka", "right", "red");
    ScoreComponent aoComponent = new ScoreComponent("Ao", "left", "blue");

    // Place components in the pane
    root.setRight(akaComponent.getComponent());
    root.setLeft(aoComponent.getComponent());
    Scene scene = new Scene(root, 600, 400);
    primaryStage.setScene(scene);
    primaryStage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}
