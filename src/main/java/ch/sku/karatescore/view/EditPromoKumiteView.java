package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.CategoryService;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.Objects;

public class EditPromoKumiteView {
    private static final String CLOSE_MODE = "Close mode ";
    private static final String STYLE_CSS = "/style.css";
    private static final String STYLE_BACKGROUND_RADIUS = "-fx-background-radius: 10px;";
    private static final String STYLE_TEXT_FILL = "-fx-text-fill: white; -fx-padding: 10;";
    private static final String STYLE_BACKGROUND_AO_COLOR = "-fx-background-color: #007bff; ";
    private static final String STYLE_BACKGROUND_AKA_COLOR = "-fx-background-color: #dc3545; ";
    @Getter
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final Participant aka;
    private final Participant ao;

    private final ScoreService scoreService;
    private final PenaltyService penaltyService;
    private final CategoryService categoryService;
    private final String modeName;
    private Stage currentModeStage; // Reference to the current mode stage

    public EditPromoKumiteView(Participant aka, Participant ao, ScoreService scoreService, PenaltyService penaltyService, CategoryService categoryService, Stage currentModeStage, String modeName) {
        this.aka = aka;
        this.ao = ao;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.categoryService = categoryService;
        this.stage = new Stage();
        this.currentModeStage = currentModeStage; // Set the current mode stage
        this.modeName = modeName; // Set the name of the clicked button
        initializeUI();
    }

    private Label getParticipantHeader(Participant participant, ParticipantType participantName) {
        Label header = new Label();
        header.textProperty().bind(Bindings.createStringBinding(() -> participantName + " - Total Points: " + scoreService.getTotalScoreProperty(participantName).get(), scoreService.getTotalScoreProperty(participantName)));
        header.getStyleClass().add("header");

        if (participant.getParticipantType() == ParticipantType.AKA) {
            header.setStyle(STYLE_BACKGROUND_AKA_COLOR + STYLE_TEXT_FILL + STYLE_BACKGROUND_RADIUS);
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            header.setStyle(STYLE_BACKGROUND_AO_COLOR + STYLE_TEXT_FILL + STYLE_BACKGROUND_RADIUS);
        }

        return header;
    }

    private Label getScoreLabel(Participant participant, ScoreType scoreType) {
        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(Bindings.createStringBinding(() -> scoreType + ": " + scoreService.getScoreProperty(participant.getParticipantType(), scoreType).get(), scoreService.getScoreProperty(participant.getParticipantType(), scoreType)));

        return scoreLabel;
    }

    public void initializeUI() {
        addCSSStyling();

        introduceScene();
        setupMainLayout();
        introduceWindowCloseEvent();
    }

    private void addCSSStyling() {
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLE_CSS)).toExternalForm());
    }

    private HBox createCategoryPanel() {
        HBox categoryPanel = createCategoryPanelLayout();
        Label categoryLabel = createCategoryLabel();
        HBox.setHgrow(categoryLabel, Priority.ALWAYS);
        categoryLabel.setMaxWidth(Double.MAX_VALUE);

        TextField categoryText = new TextField();
        categoryText.setPromptText("Enter category name");
        HBox.setHgrow(categoryText, Priority.ALWAYS);
        categoryText.setMaxWidth(Double.MAX_VALUE);

        // ⛔ Prevent SPACE from starting/stopping timer while typing
        categoryText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
            }
        });

        Button setCategoryButton = createSetCategoryButton(categoryText);
        categoryPanel.getChildren().addAll(categoryLabel, categoryText, setCategoryButton);
        return categoryPanel;
    }

    private HBox createCategoryPanelLayout() {
        HBox categoryPanel = new HBox(10);
        categoryPanel.setPadding(new Insets(20));
        categoryPanel.getStyleClass().add("category-panel");
        return categoryPanel;
    }

    private Label createCategoryLabel() {

        Label categoryLabel = new Label();
        categoryLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            String categoryInfo = categoryService.getCategoryInfo();
            return categoryInfo.isEmpty() ? "Enter category name" : categoryInfo;
        }, categoryService.categoryInfoProperty())); // Assuming categoryInfoProperty() returns a property
        return categoryLabel;
    }

    private Button createSetCategoryButton(TextField categoryText) {
        Button setCategoryButton = new Button("Set");
        setCategoryButton.setOnAction(e -> categoryService.setCategoryInfo(categoryText.getText()));
        return setCategoryButton;
    }

    private void setupMainLayout() {
        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setFillHeight(true); // <— Important!

        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO);
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA);
        HBox categoryPanel = createCategoryPanel();

        participantAO.setMinHeight(0);
        participantAKA.setMinHeight(0);
        timerPanel.setMinHeight(0);

        configureLayoutHGrow(participantAO, timerPanel, participantAKA);
        mainLayout.getChildren().addAll(participantAO, timerPanel, participantAKA);
        root.setTop(categoryPanel);
        root.setCenter(mainLayout);
    }

    private void configureLayoutHGrow(Node... nodes) {
        for (Node node : nodes)
            HBox.setHgrow(node, Priority.ALWAYS);
    }

    private Scene introduceScene() {
        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource(STYLE_CSS).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Promo kumite Mode");
        stage.setMaximized(true);
        stage.show();
        return scene;
    }

    private void introduceWindowCloseEvent() {
        stage.setOnCloseRequest(e -> closeAllStages());
    }

    private void closeAllStages() {
        if (currentModeStage != null) {
            currentModeStage.close();
            currentModeStage = null;
            stage.close();
        }
    }

    private void addButtonControls(VBox panel, Participant participant) {
        setupScoreControl(panel, participant, ScoreType.YUKO);
        setupScoreControl(panel, participant, ScoreType.WAZARI);
    }

    private void setupScoreControl(VBox panel, Participant participant, ScoreType scoreType) {
        Button btnAdd = new Button("+ " + scoreType.getStringValue());
        Button btnRemove = new Button("- " + scoreType.getStringValue());

        btnAdd.setOnAction(e -> scoreService.addScore(participant.getParticipantType(), scoreType));
        btnRemove.setOnAction(e -> scoreService.subtractScore(participant.getParticipantType(), scoreType));

        HBox scoreControls = new HBox(5, btnAdd, btnRemove);
        scoreControls.setAlignment(Pos.CENTER);
        panel.getChildren().add(scoreControls);
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantName) {
        VBox panel = new VBox(25);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.getStyleClass().add("participant-panel");

        // --- Header (AKA / AO - Total Points)
        Label header = getParticipantHeader(participant, participantName);

        // --- Score columns
        VBox yukoBox = createScoreColumn(participant, ScoreType.YUKO);
        VBox wazariBox = createScoreColumn(participant, ScoreType.WAZARI);
        VBox ipponBox = createScoreColumn(participant, ScoreType.IPPON);

        HBox scoreGrid = new HBox(10, yukoBox, wazariBox, ipponBox);
        scoreGrid.setAlignment(Pos.CENTER);

        // --- Penalties
        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, true);

        // --- Combine all
        panel.getChildren().addAll(header, scoreGrid, penaltyComponent.getComponent());
        return panel;
    }

    /**
     * Creates one vertical column for a score type: label on top, +/- buttons below.
     */
    private VBox createScoreColumn(Participant participant, ScoreType type) {
        Label scoreLabel = getScoreLabel(participant, type);
        scoreLabel.setAlignment(Pos.CENTER);

        Button btnAdd = new Button("+ " + type.getStringValue());
        Button btnRemove = new Button("- " + type.getStringValue());

        // ❌ remove: btnAdd.setPrefWidth(140); btnRemove.setPrefWidth(140);

        // ✅ let buttons size to text and never shrink below it
        btnAdd.setPrefWidth(Region.USE_COMPUTED_SIZE);
        btnRemove.setPrefWidth(Region.USE_COMPUTED_SIZE);
        btnAdd.setMinWidth(Region.USE_PREF_SIZE);
        btnRemove.setMinWidth(Region.USE_PREF_SIZE);

        // (optional) allow wrap instead of ellipsis if parent width ever gets tight
        // btnAdd.setWrapText(true);
        // btnRemove.setWrapText(true);

        btnAdd.setOnAction(e -> scoreService.addScore(participant.getParticipantType(), type));
        btnRemove.setOnAction(e -> scoreService.subtractScore(participant.getParticipantType(), type));

        VBox box = new VBox(5, scoreLabel, btnAdd, btnRemove);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox createTimerPanel() {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.setAlignment(Pos.BOTTOM_CENTER);
        timerPanel.getStyleClass().add("timer-panel");
        // User input fields for minutes and seconds


        VBox resetMiddle = new VBox(10);
        Button resetAll = new Button("Reset ALL");
        resetAll.setPrefWidth(Region.USE_COMPUTED_SIZE);
        resetAll.setMinWidth(Region.USE_PREF_SIZE);
        resetAll.setStyle("-fx-text-fill: orange");
        resetAll.setOnAction(e -> {
            scoreService.resetScores();
            penaltyService.resetPenalties();
            categoryService.resetCategoryInfo();

        });
        resetMiddle.setAlignment(Pos.CENTER);
        resetMiddle.getChildren().add(resetAll);
        Button closeModeButton = new Button(CLOSE_MODE + modeName);
        closeModeButton.setVisible(currentModeStage != null);
        closeModeButton.setOnAction(e -> closeAllStages());
        closeModeButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
        closeModeButton.setMinWidth(Region.USE_PREF_SIZE);

        HBox bottomButtons = new HBox(15, closeModeButton, resetAll);
        bottomButtons.setAlignment(Pos.CENTER);

        // Final layout
        timerPanel.getChildren().addAll(bottomButtons);

        return timerPanel;
    }

}
