package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.CategoryService;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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
    private final CategoryService categoryService;

    public PromoKumiteView(
            Participant aka,
            Participant ao,
            ScoreService scoreService,
            PenaltyService penaltyService,
            CategoryService categoryService
    ) {
        this.aka = aka;
        this.ao = ao;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.categoryService = categoryService;
        this.stage = new Stage();
        initializeUI();
    }

    private void initializeUI() {
        StackPane root = new StackPane();

        // === Category ===
        HBox categoryBox = new HBox();
        categoryBox.setAlignment(Pos.TOP_LEFT);
        categoryBox.setPadding(new Insets(20, 50, 0, 50));

        Label categoryLabel = new Label();
        categoryLabel.textProperty().bind(categoryService.categoryInfoProperty());
        categoryLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        categoryBox.getChildren().add(categoryLabel);

        // === Participants panels ===
        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA);
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO);

        HBox participantsBox = new HBox(10, akaPanel, aoPanel);
        participantsBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(akaPanel, Priority.ALWAYS);
        HBox.setHgrow(aoPanel, Priority.ALWAYS);

        // === Layout ===
        root.getChildren().addAll(participantsBox, categoryBox);
        StackPane.setAlignment(categoryBox, Pos.TOP_LEFT);

        Scene scene = new Scene(root, 1920, 1080);
        scene.setFill(javafx.scene.paint.Color.BLACK);
        stage.setScene(scene);
        stage.setTitle("Promo Kumite");
        setFullScreen();
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantType) {
        // Background for each side
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

// === Breakdown (Yuko, Waza-ari, Ippon) ===
        VBox yukoBox   = createScoreBox("Yuko",     scoreService.getScoreProperty(participantType, ScoreType.YUKO));
        VBox wazariBox = createScoreBox("Waza-ari", scoreService.getScoreProperty(participantType, ScoreType.WAZARI));


// ↓ Reduce spacing from 80 → 30 (aligns better with penalty width)
        HBox breakdown = new HBox(30, yukoBox, wazariBox);
        breakdown.setAlignment(Pos.CENTER);

        VBox scoreBlock = new VBox(5, totalScoreLabel, breakdown);
        scoreBlock.setAlignment(Pos.CENTER);

// === Penalties ===
        VBox penaltyBox = new VBox(5);
        penaltyBox.setAlignment(Pos.CENTER);
        penaltyBox.setPadding(new Insets(10, 0, 0, 0));
        addPenaltyLabels(participant, penaltyBox);

// ↓ Adjust vertical spacing (closer alignment between breakdown & penalties)
        VBox centerColumn = new VBox(10, scoreBlock, penaltyBox);
        centerColumn.setAlignment(Pos.CENTER);


        // === Stack layout ===
        side.getChildren().add(centerColumn);
        StackPane.setAlignment(centerColumn, Pos.CENTER);

        // Wrapper for consistency
        VBox wrapper = new VBox(side);
        VBox.setVgrow(side, Priority.ALWAYS);
        wrapper.setFillWidth(true);
        return wrapper;
    }

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
