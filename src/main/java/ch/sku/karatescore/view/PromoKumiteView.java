package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
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

public class PromoKumiteView {
    @Getter
    private final Stage stage;
    private final Participant aka;
    private final Participant ao;
    private final ScoreService scoreService;
    private final PenaltyService penaltyService;

    public PromoKumiteView(Participant aka, Participant ao, ScoreService scoreService, PenaltyService penaltyService) {
        this.stage = new Stage();
        this.aka = aka;
        this.ao = ao;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
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

        root.getChildren().add(participantsBox);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        setFullScreen();
        stage.setTitle("Karate Match Scoreboard");
    }
//TODO mettre la separation - entre les scores
    private VBox createParticipantPanel(Participant participant, ParticipantType participantType) {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + (participantType == ParticipantType.AKA ? "#dc3545" : "#007bff") + "; -fx-text-fill: white;");
        panel.setAlignment(Pos.CENTER);
        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(Bindings.format("%d", scoreService.getTotalScoreProperty(participant.getParticipantType())));
        totalScoreLabel.setStyle("-fx-font-size: 200px; -fx-text-fill: white;");

        Label detailedScoreLabel = new Label();
        detailedScoreLabel.textProperty().bind(Bindings.format("Yuko: %d, Waza-ari: %d",
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI)));
        detailedScoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        panel.getChildren().addAll(totalScoreLabel, detailedScoreLabel);
        addPenaltyLabels(participant, panel);

        return panel;
    }

    private void addPenaltyLabels(Participant participant, VBox panel) {
        HBox penaltyContainer = new HBox(10);
        penaltyContainer.setAlignment(Pos.CENTER);
        penaltyContainer.setMinHeight(100);  // Fixed height
        penaltyContainer.setPrefHeight(100); // Fixed height

        for (PenaltyType penaltyType : PenaltyType.values()) {
            Label penaltyLabel = new Label(penaltyType.toString());
            penaltyLabel.getStyleClass().add("penalty-label");
            penaltyLabel.setMinSize(60, 60);
            penaltyLabel.setMaxSize(60, 60);
            penaltyLabel.setAlignment(Pos.CENTER);
            penaltyLabel.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-font-size: 40px;");
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
