package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.TimerService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WKFView {
    private final Stage stage;
    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;

    public WKFView(Participant aka, Participant ao, TimerService timerService) {
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.stage = new Stage();
        initializeUI();
    }

    private void initializeUI() {
        HBox root = new HBox();

        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA);
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO);
        StackPane middlePanel = new StackPane(createTimerDisplay());

        HBox.setHgrow(akaPanel, Priority.ALWAYS);
        HBox.setHgrow(aoPanel, Priority.ALWAYS);
        akaPanel.setMaxWidth(Double.MAX_VALUE);
        aoPanel.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(akaPanel, middlePanel, aoPanel);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        setFullScreen();
        stage.setTitle("Karate Match Scoreboard");
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType type) {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: " + (type == ParticipantType.AKA ? "#ff0000" : "#0000ff") + "; -fx-text-fill: white;");
        panel.setAlignment(Pos.CENTER);

        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(Bindings.format("%s Scores - Yuko: %d, Waza-ari: %d, Ippon: %d", type, participant.yukoScoreProperty(), participant.wazaAriScoreProperty(), participant.ipponScoreProperty()));
        addPenaltyLabels(participant, type, panel);
        panel.getChildren().add(scoreLabel);
        return panel;
    }

    private StackPane createTimerDisplay() {
        StackPane timerDisplay = new StackPane();
        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d", timerService.minutesProperty(), timerService.secondsProperty()));
        timerLabel.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");
        timerDisplay.getChildren().add(timerLabel);
        return timerDisplay;
    }


    private void addPenaltyLabels(Participant participant, ParticipantType type, VBox panel) {
        for (PenaltyType penaltyType : PenaltyType.values()) {
            Label penaltyLabel = new Label();
            penaltyLabel.textProperty().bind(Bindings.when(participant.getPenaltyProperty(penaltyType)).then(penaltyType.toString() + ": Given").otherwise(penaltyType + ": Not Given"));
            panel.getChildren().add(penaltyLabel);
        }
    }


    public void show() {
        stage.show();
    }

    private void setFullScreen() {
        Screen screen = Screen.getPrimary();  // Adjust this if multi-screen setup
        stage.setX(screen.getVisualBounds().getMinX());
        stage.setY(screen.getVisualBounds().getMinY());
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setFullScreen(true);
    }

}