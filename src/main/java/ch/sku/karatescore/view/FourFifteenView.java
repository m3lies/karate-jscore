package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
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

        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA);
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO);
        StackPane middlePanel = new StackPane(createIntervalTimerDisplay());

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
        addPenaltyLabels(participant, panel);
        return panel;
    }

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

    private StackPane createIntervalTimerDisplay() {
        StackPane intervalDisplay = new StackPane();
        Label intervalLabel1 = new Label();
        Label intervalLabel2 = new Label();
        Label intervalLabel3 = new Label();
        Label intervalLabel4 = new Label();
        Label periodLabel = new Label();

        periodLabel.textProperty().bind(Bindings.format("Period %02d", timerService.periodProperty()));

        intervalLabel1.textProperty().bind(Bindings.format("Interval Time 1: %02d seconds", timerService.intervalSecondsProperty1()));
        intervalLabel1.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");

        intervalLabel2.textProperty().bind(Bindings.format("Interval Time 2: %02d seconds", timerService.intervalSecondsProperty2()));
        intervalLabel2.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");

        intervalLabel3.textProperty().bind(Bindings.format("Interval Time 3: %02d seconds", timerService.intervalSecondsProperty3()));
        intervalLabel3.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");

        intervalLabel4.textProperty().bind(Bindings.format("Interval Time 4: %02d seconds", timerService.intervalSecondsProperty4()));
        intervalLabel4.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");

        VBox intervalLabels = new VBox(intervalLabel1, intervalLabel2, intervalLabel3, intervalLabel4, periodLabel);
        intervalLabels.setAlignment(Pos.CENTER);

        intervalDisplay.getChildren().add(intervalLabels);
        return intervalDisplay;
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
