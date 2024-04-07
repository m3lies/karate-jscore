package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.components.ScoreComponent;
import ch.sku.karatescore.components.TimerComponent;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;


public class KarateScorboardApp extends Application {    @Override
public void start(Stage primaryStage) {
    primaryStage.setTitle("Karate Scoreboard");

    primaryStage.setTitle("Karate Scoreboard");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20, 50, 20, 50)); // Adjust padding as needed

    // Initialize components for Aka and Ao
    ScoreComponent akaComponent = new ScoreComponent(ParticipantType.AKA, "right", "#FF0000");
    ScoreComponent aoComponent = new ScoreComponent(ParticipantType.AO, "left", "#0000FF");
    TimerComponent timerComponent = new TimerComponent();

    // Place components in the pane
    root.setRight(akaComponent.getComponent());
    root.setLeft(aoComponent.getComponent());
    root.setCenter(timerComponent.getComponent());
    Scene scene = new Scene(root, 600, 400);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}
