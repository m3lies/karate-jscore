package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.model.MatchData;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.TimerService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class FourFifteenView {
    private final Stage stage;
    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;
    private final PenaltyService penaltyService;

    public FourFifteenView(Participant aka, Participant ao, TimerService timerService, PenaltyService penaltyService) {
        this.stage =  new Stage();
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
        HBox penaltyContainer = new HBox(10); // Create an HBox with spacing
        penaltyContainer.setAlignment(Pos.CENTER); // Center the penalties horizontally

        for (PenaltyType penaltyType : PenaltyType.values()) {
            Label penaltyLabel = new Label(penaltyType.toString());
            penaltyLabel.getStyleClass().add("penalty-label"); // Applying styles

            // Bind the visible property to whether the penalty is given or not
            penaltyLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participant.getParticipantType(), penaltyType));
            penaltyLabel.managedProperty().bind(penaltyLabel.visibleProperty());

            // Bind opacity for smooth visual transitions
            penaltyLabel.opacityProperty().bind(Bindings.when(
                            penaltyService.getPenaltyProperty(participant.getParticipantType(), penaltyType))
                    .then(1.0).otherwise(0.0));

            penaltyContainer.getChildren().add(penaltyLabel);
        }

        panel.getChildren().add(penaltyContainer);
    }

    private StackPane createTimerDisplay() {
        StackPane timerDisplay = new StackPane();
        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d", timerService.minutesProperty(),
                timerService.secondsProperty()));
        timerLabel.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");
        timerDisplay.getChildren().add(timerLabel);
        return timerDisplay;
    }

    private StackPane createIntervalTimerDisplay() {
        StackPane intervalDisplay = new StackPane();
        Label intervalLabel = new Label();
        intervalLabel.textProperty().bind(Bindings.format("Interval Time: %02d seconds", timerService.intervalSecondsProperty()));
        intervalLabel.setStyle("-fx-font-size: 34px; -fx-text-fill: black;");
        intervalDisplay.getChildren().add(intervalLabel);
        return intervalDisplay;
    }

    private void setFullScreen() {
        Screen screen = Screen.getPrimary();  // Adjust this if multi-screen setup
        stage.setX(screen.getVisualBounds().getMinX());
        stage.setY(screen.getVisualBounds().getMinY());
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setFullScreen(true);
    }
    public void show() {
        stage.show();
    }

}
