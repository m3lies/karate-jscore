package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.SenshuService;
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
import lombok.Getter;

public class WKFView {
    @Getter
    private final Stage stage;
    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;
    private final ScoreService scoreService;
    private final PenaltyService penaltyService;
    private final SenshuService senshuService;

    public WKFView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService) {
        this.stage = new Stage();
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.senshuService = senshuService;
        initializeUI();
    }

    private void initializeUI() {
        StackPane root = new StackPane();

        HBox participantsBox = new HBox(10);

        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA);
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO);

        HBox.setHgrow(akaPanel, Priority.ALWAYS);
        HBox.setHgrow(aoPanel, Priority.ALWAYS);
        akaPanel.setMaxWidth(Double.MAX_VALUE);
        aoPanel.setMaxWidth(Double.MAX_VALUE);

        participantsBox.getChildren().addAll(akaPanel, aoPanel);

        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d", timerService.minutesProperty(), timerService.secondsProperty()));
        timerLabel.setStyle("-fx-font-size: 100px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 1); -fx-padding: 10px;");
        //TODO reduire l'encadré en haut et en bas et bords arrondis

        StackPane timerPane = new StackPane(timerLabel);
        StackPane.setAlignment(timerPane, Pos.CENTER);

        root.getChildren().addAll(participantsBox, timerPane);

        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        setFullScreen();
        stage.setTitle("Karate Match Scoreboard");
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantType) {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + (participantType == ParticipantType.AKA ? "#dc3545" : "#007bff") + "; -fx-text-fill: white;");
        panel.setAlignment(Pos.CENTER);

        HBox scoreSenshuBox = new HBox(10);
        scoreSenshuBox.setAlignment(Pos.CENTER);

        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(Bindings.format("%d", scoreService.getTotalScoreProperty(participant.getParticipantType())));
        totalScoreLabel.setStyle("-fx-font-size: 200px; -fx-text-fill: white;");

        Label detailedScoreLabel = new Label();
        detailedScoreLabel.textProperty().bind(Bindings.format("Yuko %d    Waza-ari %d    Ippon %d",
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON)));
        detailedScoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        VBox scoreBox = new VBox(10, totalScoreLabel, detailedScoreLabel);
        scoreBox.setAlignment(Pos.CENTER);

        //TODO add senshu en petite taille

        Label senshuLabel = new Label("●");
        senshuLabel.setStyle("-fx-font-size: 100px; -fx-text-fill: yellow;");
        senshuLabel.visibleProperty().bind(senshuService.getSenshuProperty(participant.getParticipantType()));

        if (participantType == ParticipantType.AKA) {
            scoreSenshuBox.getChildren().addAll(senshuLabel, scoreBox);
        } else {
            scoreSenshuBox.getChildren().addAll(scoreBox, senshuLabel);
        }

        panel.getChildren().addAll(scoreSenshuBox);
        addPenaltyLabels(participant, panel);
        return panel;
    }

    //TODO encadrer les avertissements fond blanc, police couleur aka, ao

    private void addPenaltyLabels(Participant participant, VBox panel) {
        HBox penaltyContainer = new HBox(10);
        penaltyContainer.setAlignment(Pos.CENTER);

        for (PenaltyType penaltyType : PenaltyType.values()) {
            Label penaltyLabel = new Label(penaltyType.toString());
            penaltyLabel.getStyleClass().add("penalty-label");
            penaltyLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participant.getParticipantType(), penaltyType));
            penaltyLabel.managedProperty().bind(penaltyLabel.visibleProperty());
            penaltyContainer.getChildren().add(penaltyLabel);
        }

        panel.getChildren().add(penaltyContainer);
    }

    private void setFullScreen() {
        Screen screen = Screen.getPrimary();
        stage.setX(screen.getVisualBounds().getMinX());
        stage.setY(screen.getVisualBounds().getMinY());
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setFullScreen(true);
    }
}
