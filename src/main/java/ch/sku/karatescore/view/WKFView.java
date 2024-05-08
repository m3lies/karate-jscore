package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.MatchData;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WKFView {
    private final Stage stage;

    public WKFView() {
        this.stage = new Stage();
        initializeUI();
    }
    private void initializeUI() {
        // Create the layout
        HBox root = new HBox();

        VBox akaPanel = createParticipantPanel("AKA", Color.RED);
        VBox aoPanel = createParticipantPanel("AO", Color.BLUE);
        StackPane middlePanel = new StackPane();
        middlePanel.getChildren().add(createTimerDisplay());

        // Assigning the full width for responsive layout
        HBox.setHgrow(akaPanel, Priority.ALWAYS);
        HBox.setHgrow(aoPanel, Priority.ALWAYS);
        akaPanel.setMaxWidth(Double.MAX_VALUE);
        aoPanel.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(akaPanel, middlePanel, aoPanel);

        // Adjust stage for full screen
        stage.setTitle("Karate Match Scoreboard");
        stage.setScene(new javafx.scene.Scene(root, 800, 600));
        setFullScreen();
    }
    private Label createTimerDisplay() {
        Label timerLabel = new Label("1:30");
        timerLabel.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");
        return timerLabel;
    }

    private VBox createParticipantPanel(String name, Color color) {
        VBox panel = new VBox();
        panel.setStyle("-fx-background-color: " + toRgbString(color) + ";");
        Label nameLabel = new Label(name + " Data Here");
        nameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        panel.getChildren().add(nameLabel);
        panel.setAlignment(Pos.CENTER);
        return panel;
    }
    private String toRgbString(Color c) {
        return String.format("rgb(%d,%d,%d)",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }

    private void setFullScreen() {
        Screen screen = Screen.getScreens().size() > 1 ? Screen.getScreens().get(1) : Screen.getPrimary();
        stage.setX(screen.getBounds().getMinX());
        stage.setY(screen.getBounds().getMinY());
        stage.setWidth(screen.getBounds().getWidth());
        stage.setHeight(screen.getBounds().getHeight());
        stage.setFullScreen(true);
    }



    private VBox createParticipantScoreLayout(MatchData matchData, ParticipantType type) {
        VBox dataLayout = new VBox(10);
        dataLayout.setPadding(new Insets(15));

        // Participant Label
        Label participantLabel = new Label(type.name());

        // Labels for scores and penalties
        Label yukoLabel = new Label();
        yukoLabel.textProperty().bind(matchData.yukoProperty(type).asString().concat(" Yuko"));

        Label wazaAriLabel = new Label();
        wazaAriLabel.textProperty().bind(matchData.wazaAriProperty(type).asString().concat(" Waza-ari"));

        Label ipponLabel = new Label();
        ipponLabel.textProperty().bind(matchData.ipponProperty(type).asString().concat(" Ippon"));

        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(matchData.totalScoreProperty(type).asString().concat("Total"));

        addPenaltyLabels(matchData, type, dataLayout);

        // Add all labels to the VBox
        dataLayout.getChildren().addAll(participantLabel, yukoLabel, wazaAriLabel, ipponLabel, totalScoreLabel);

        return dataLayout;

    }

    private void addPenaltyLabels(MatchData matchData, ParticipantType participantType, VBox parent) {
        for (PenaltyType penaltyType : PenaltyType.values()) {
            Label penaltyLabel = new Label();
            penaltyLabel.textProperty().bind(Bindings.when(matchData.penaltyProperty(participantType, penaltyType))
                    .then(penaltyType.toString() + ": Given")
                    .otherwise(penaltyType + ": Not Given"));
            parent.getChildren().add(penaltyLabel);
        }
    }


}
