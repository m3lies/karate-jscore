package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.*;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

    public EditPromoKumiteView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService, CategoryService categoryService, Stage currentModeStage, String modeName) {
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
        setupMainLayout();
        introduceScene();
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
        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO);
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA);
        HBox categoryPanel = createCategoryPanel();
        VBox middlePanel = createTimerPanel();
        configureLayoutHGrow(participantAO, middlePanel, participantAKA);
        mainLayout.getChildren().addAll(participantAO, middlePanel, participantAKA);
        root.setTop(categoryPanel);
        root.setCenter(mainLayout);

        BorderPane paneForCloseModeButton = new BorderPane();
        Button closeModeButton = new Button(CLOSE_MODE + modeName);
        closeModeButton.setVisible(currentModeStage != null); // Set initial visibility
        closeModeButton.setOnAction(e -> {
            closeAllStages();
        });
        paneForCloseModeButton.setBottom(closeModeButton);
        closeModeButton.setAlignment(Pos.BOTTOM_CENTER);
        middlePanel.getChildren().add(paneForCloseModeButton);
    }

    private void configureLayoutHGrow(Node... nodes) {
        for (Node node : nodes)
            HBox.setHgrow(node, Priority.ALWAYS);
    }

    private void introduceScene() {
        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource(STYLE_CSS).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Promo kumite Mode");
        stage.setMaximized(true);
        stage.show();
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
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("participant-panel");


        HBox scoreControls = new HBox(10, getScoreLabel(participant, ScoreType.YUKO), getScoreLabel(participant, ScoreType.WAZARI));
        scoreControls.setAlignment(Pos.CENTER);

        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, true);

        panel.getChildren().addAll(getParticipantHeader(participant, participantName), scoreControls);
        addButtonControls(panel, participant);
        panel.getChildren().add(penaltyComponent.getComponent());
        return panel;
    }
    private VBox createTimerPanel() {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.getStyleClass().add("timer-panel");
     // User input fields for minutes and seconds


        VBox resetMiddle = new VBox(10);
        Button resetAll = new Button("Reset ALL");
        resetAll.setStyle("-fx-text-fill: orange");
        resetAll.setOnAction(e -> {
            scoreService.resetScores();
            penaltyService.resetPenalties();
            categoryService.resetCategoryInfo();

        });
        resetMiddle.setAlignment(Pos.CENTER);
        resetMiddle.getChildren().add(resetAll);

        timerPanel.getChildren().addAll(resetMiddle);

        return timerPanel;
    }

}
