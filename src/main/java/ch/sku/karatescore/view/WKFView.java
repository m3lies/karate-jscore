package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.*;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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
    private final CategoryService categoryService;

    public WKFView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService, CategoryService categoryService) {
        this.categoryService = categoryService;
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
        StackPane mainPane = new StackPane();

        HBox participantsBox = new HBox(10);
        HBox categoryBox = new HBox();
        Label categoryLabel = new Label();
        categoryLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2; -fx-padding: 10px; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        categoryLabel.textProperty().bind(Bindings.format("%s",categoryService.categoryInfoProperty()));
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        categoryBox.getChildren().addAll(categoryLabel);
        categoryBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(categoryBox, Priority.ALWAYS);
        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA);
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO);

        HBox.setHgrow(akaPanel, Priority.ALWAYS);
        HBox.setHgrow(aoPanel, Priority.ALWAYS);
        akaPanel.setMaxWidth(Double.MAX_VALUE);
        aoPanel.setMaxWidth(Double.MAX_VALUE);

        participantsBox.getChildren().addAll(akaPanel, aoPanel);

        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d", timerService.minutesProperty(), timerService.secondsProperty()));
        timerLabel.setStyle("-fx-font-size: 100px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 1);-fx-background-radius: 10px; -fx-padding:0em 0.2em 0em 0.2em;;");

        StackPane timerPane = new StackPane(timerLabel);
        StackPane.setAlignment(timerPane, Pos.CENTER);

        mainPane.getChildren().addAll(participantsBox, timerPane);

        // Adding participant type labels
        Label akaTypeLabel = createParticipantTypeLabel(ParticipantType.AKA);
        Label aoTypeLabel = createParticipantTypeLabel(ParticipantType.AO);
        StackPane.setAlignment(akaTypeLabel, Pos.TOP_LEFT);
        StackPane.setAlignment(aoTypeLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(akaTypeLabel, new Insets(50, 50, 0, 50)); // Adjust margin to bring it closer to the center and down
        StackPane.setMargin(aoTypeLabel, new Insets(50, 50, 0, 50)); // Adjust margin to bring it closer to the center and down
        mainPane.getChildren().addAll(categoryBox, akaTypeLabel, aoTypeLabel);
        root.getChildren().addAll(mainPane);

        StackPane.setAlignment(mainPane, Pos.CENTER);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        setFullScreen();
        stage.setTitle("WKF View");
    }

    private Label createParticipantTypeLabel(ParticipantType participantType) {
        Label label = new Label(participantType.name());
        label.setStyle("-fx-font-size: 50px; -fx-text-fill: white;-fx-font-weight: bold ; -fx-padding: 5px;");
        return label;
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantType) {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + (participantType == ParticipantType.AKA ? "#dc3545" : "#007bff") + "; -fx-text-fill: white;");
        panel.setAlignment(Pos.CENTER);

        HBox scoreSenshuBox = new HBox(10);
        scoreSenshuBox.setAlignment(Pos.CENTER);

        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(Bindings.format("%d", scoreService.getTotalScoreProperty(participant.getParticipantType())));
        totalScoreLabel.setStyle("-fx-font-size: 300px; -fx-text-fill: white;");

        Label detailedScoreLabel = new Label();
        detailedScoreLabel.textProperty().bind(Bindings.format("Yuko %d    Waza-ari %d    Ippon %d",
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON)
        ));
        detailedScoreLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white;");

        VBox scoreBox = new VBox(10, totalScoreLabel, detailedScoreLabel);
        scoreBox.setAlignment(Pos.CENTER);

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

    private void setFullScreen() {
        Screen screen = Screen.getPrimary();
        stage.setX(screen.getVisualBounds().getMinX());
        stage.setY(screen.getVisualBounds().getMinY());
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setFullScreen(true);
    }
}
