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

        categoryLabel.textProperty().bind(categoryService.categoryInfoProperty());
        categoryLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        categoryBox.getChildren().addAll(categoryLabel);
        categoryBox.setAlignment(Pos.TOP_LEFT);
        categoryBox.setPadding(new Insets(0, 50, 0, 0));
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
        StackPane.setMargin(akaTypeLabel, new Insets(100, 100, 0, 50)); // Adjust margin to bring it closer to the center and down
        StackPane.setMargin(aoTypeLabel, new Insets(100, 50, 0, 100)); // Adjust margin to bring it closer to the center and down
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
        // === Background for each side ===
        StackPane side = new StackPane();
        side.setStyle("-fx-background-color: " +
                (participantType == ParticipantType.AKA ? "#dc3545" : "#007bff") +
                "; -fx-text-fill: white;");
        side.setPadding(new Insets(0));

        // === TOTAL SCORE ===
        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(
                Bindings.format("%d", scoreService.getTotalScoreProperty(participant.getParticipantType()))
        );
        totalScoreLabel.setStyle("-fx-font-size: 320px; -fx-text-fill: white; -fx-font-weight: bold;");
        totalScoreLabel.setAlignment(Pos.CENTER);
        totalScoreLabel.setPrefWidth(400); // adjust width to your layout

        // === SENSU LABEL ===
        Label senshuLabel = new Label("●");
        senshuLabel.setStyle("-fx-font-size: 100px; -fx-text-fill: yellow;");
        senshuLabel.visibleProperty().bind(senshuService.getSenshuProperty(participant.getParticipantType()));

        // ✅ Put score and senshu in an HBox
        HBox scoreRow = new HBox(20);
        scoreRow.setAlignment(Pos.CENTER);

        if (participantType == ParticipantType.AKA) {
            // Senshu (yellow dot) on the LEFT side for AKA
            scoreRow.getChildren().addAll(senshuLabel, totalScoreLabel);
        } else {
            // Senshu (yellow dot) on the RIGHT side for AO
            scoreRow.getChildren().addAll(totalScoreLabel, senshuLabel);
        }

        // === Breakdown (Yuko, Waza-ari, Ippon) ===
        VBox yukoBox   = createScoreBox("Yuko",     scoreService.getScoreProperty(participantType, ScoreType.YUKO));
        VBox wazariBox = createScoreBox("Waza-ari", scoreService.getScoreProperty(participantType, ScoreType.WAZARI));
        VBox ipponBox  = createScoreBox("Ippon",    scoreService.getScoreProperty(participantType, ScoreType.IPPON));

        HBox breakdown = new HBox(30, yukoBox, wazariBox, ipponBox);
        breakdown.setAlignment(Pos.CENTER);

        VBox scoreBlock = new VBox(25, scoreRow, breakdown);
        scoreBlock.setAlignment(Pos.CENTER);

        // === Penalties ===
        VBox penaltyBox = new VBox(10);
        penaltyBox.setAlignment(Pos.CENTER);
        penaltyBox.setPadding(new Insets(10, 0, 0, 0));
        addPenaltyLabels(participant, penaltyBox);

        VBox centerColumn = new VBox(45, scoreBlock, penaltyBox);
        centerColumn.setAlignment(Pos.CENTER);

        side.getChildren().add(centerColumn);
        StackPane.setAlignment(centerColumn, Pos.CENTER);

        VBox wrapper = new VBox(side);
        VBox.setVgrow(side, Priority.ALWAYS);
        wrapper.setFillWidth(true);
        return wrapper;
    }



    /**
     * Helper to create one vertical column for a score type.
     */
    private VBox createScoreBox(String label, javafx.beans.property.IntegerProperty scoreProperty) {
        Label typeLabel = new Label(label);
        typeLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label valueLabel = new Label();
        valueLabel.textProperty().bind(Bindings.format("%d", scoreProperty));
        valueLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: white;");

        VBox box = new VBox(10, typeLabel, valueLabel);
        box.setAlignment(Pos.CENTER);
        return box;
    }


    private void addPenaltyLabels(Participant participant, VBox panel) {
        HBox penaltyContainer = new HBox(10);
        penaltyContainer.setAlignment(Pos.CENTER);

        for (PenaltyType penaltyType : PenaltyType.values()) {
            VBox penaltyBox = new VBox(5);
            penaltyBox.setAlignment(Pos.CENTER);

            Label penaltyNameLabel = new Label(penaltyType.getName());
            penaltyNameLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2; -fx-padding: 10px; -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-font-weight: bold;");
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
