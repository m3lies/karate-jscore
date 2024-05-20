package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.TimerService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;

public class FourFifteenView {
    @Getter
    private final Stage stage;
    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;
    private final PenaltyService penaltyService;

    public FourFifteenView(Participant aka, Participant ao, TimerService timerService, PenaltyService penaltyService) {
        this.stage = new Stage();
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.penaltyService = penaltyService;
        initializeUI();
    }

    private void initializeUI() {
        HBox root = new HBox();

        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA, timerService.intervalSecondsProperty1(), timerService.intervalSecondsProperty3());
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO, timerService.intervalSecondsProperty2(), timerService.intervalSecondsProperty4());

        HBox.setHgrow(akaPanel, Priority.ALWAYS);
        HBox.setHgrow(aoPanel, Priority.ALWAYS);
        akaPanel.setMaxWidth(Double.MAX_VALUE);
        aoPanel.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(akaPanel, aoPanel);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        setFullScreen();
        stage.setTitle("Karate Match Scoreboard");
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType type, IntegerProperty interval1, IntegerProperty interval2) {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + (type == ParticipantType.AKA ? "#dc3545" : "#007bff") + "; -fx-text-fill: white;");
        panel.setAlignment(Pos.CENTER);

        Label intervalLabel1 = createIntervalLabel(interval1, type == ParticipantType.AKA ? 1 : 2);
        Label intervalLabel2 = createIntervalLabel(interval2, type == ParticipantType.AKA ? 3 : 4);

        VBox intervalBox = new VBox(50, intervalLabel1, intervalLabel2);
        intervalBox.setAlignment(Pos.CENTER);

        panel.getChildren().add(intervalBox);
        addPenaltyLabels(participant, panel);
        return panel;
    }

    private Label createIntervalLabel(IntegerProperty intervalProperty, int periodNumber) {
        Label intervalLabel = new Label();
        StringBinding timeBinding = createTimerStringBinding(intervalProperty);
        StringBinding styleBinding = createStyleBinding(intervalProperty, periodNumber);

        intervalLabel.textProperty().bind(timeBinding);
        intervalLabel.styleProperty().bind(styleBinding);

        return intervalLabel;
    }

    private StringBinding createTimerStringBinding(IntegerProperty intervalProperty) {
        return Bindings.createStringBinding(() -> String.format("00:%02d", intervalProperty.get()), intervalProperty);
    }

    private StringBinding createStyleBinding(IntegerProperty intervalProperty, int periodNumber) {
        return Bindings.createStringBinding(() -> {
            if (timerService.periodProperty().get() == periodNumber) {
                return "-fx-font-size: 100px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5);";
            } else if (intervalProperty.get() == 0) {
                return "-fx-font-size: 100px; -fx-text-fill: grey;";
            } else {
                return "-fx-font-size: 100px; -fx-text-fill: white;";
            }
        }, intervalProperty, timerService.periodProperty());
    }

    private void addPenaltyLabels(Participant participant, VBox panel) {
        HBox penaltyContainer = new HBox(20);
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
