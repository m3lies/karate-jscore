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
        StackPane root = new StackPane();

        HBox participantsBox = new HBox(10);

        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA, timerService.intervalSecondsProperty1(), timerService.intervalSecondsProperty3());
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO, timerService.intervalSecondsProperty2(), timerService.intervalSecondsProperty4());

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

    private VBox createParticipantPanel(Participant participant, ParticipantType type, IntegerProperty interval1, IntegerProperty interval2) {
        VBox panel = new VBox(20);
        Label participantTypeName = new Label(type.getType());
        participantTypeName.setAlignment(type == ParticipantType.AKA ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
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
//TODO change color dark red and dark blue
private void addPenaltyLabels(Participant participant, VBox panel) {
    HBox penaltyContainer = new HBox(10);
    penaltyContainer.setAlignment(Pos.CENTER);

    for (PenaltyType penaltyType : PenaltyType.values()) {
        VBox penaltyBox = new VBox(5);
        penaltyBox.setAlignment(Pos.CENTER);

        Label penaltyNameLabel = new Label(penaltyType.getName());
        penaltyNameLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2; -fx-padding: 10px; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        penaltyNameLabel.setMinSize(80, 80); // Ensure fixed size based on the preferred size
        penaltyNameLabel.setMaxSize(80, 80); // Ensure fixed size based on the preferred size

        // Set background color based on participant type
        if (participant.getParticipantType() == ParticipantType.AKA) {
            penaltyNameLabel.setStyle(penaltyNameLabel.getStyle() + "-fx-background-color: #dc3545;");
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            penaltyNameLabel.setStyle(penaltyNameLabel.getStyle() + "-fx-background-color: #007bff;");
        }

        // Bind visibility to the penalty property
        penaltyNameLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participant.getParticipantType(), penaltyType));
        penaltyNameLabel.managedProperty().bind(penaltyNameLabel.visibleProperty());
        penaltyNameLabel.setAlignment(Pos.CENTER);
        // Create a Region to reserve space for the label
        Region labelPlaceholder = new Region();
        labelPlaceholder.setMinSize(80, 80); // Ensure the same size as the label
        labelPlaceholder.setMaxSize(80, 80); // Ensure the same size as the label

        StackPane stackPane = new StackPane(labelPlaceholder, penaltyNameLabel);
        stackPane.setAlignment(Pos.CENTER);

        penaltyBox.getChildren().addAll(stackPane);
        penaltyContainer.getChildren().add(penaltyBox);
    }

    panel.getChildren().add(penaltyContainer);
}
//TODO quand ca reset, on reset aussi les penalités
    private void setFullScreen() {
        Screen screen = Screen.getPrimary();
        stage.setX(screen.getVisualBounds().getMinX());
        stage.setY(screen.getVisualBounds().getMinY());
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setFullScreen(true);
    }
}
