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

public class EditFourFifteenView {
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
    private final TimerService timerService;
    private final PenaltyService penaltyService;
    private final CategoryService categoryService;
    private final String modeName;
    private Stage currentModeStage; // Reference to the current mode stage

    public EditFourFifteenView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService, CategoryService categoryService, Stage currentModeStage, String modeName) {
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.penaltyService = penaltyService;
        this.categoryService = categoryService;
        this.stage = new Stage();
        this.currentModeStage = currentModeStage; // Set the current mode stage
        this.modeName = modeName; // Set the name of the clicked button
        initializeUI();
    }

    private static void handleTimeSet(TextField minutesInput, TextField secondsInput, TimerService timerService) {
        try {
            int mins = minutesInput.getText().trim().isEmpty() ? 0 : Integer.parseInt(minutesInput.getText().trim());
            int secs = secondsInput.getText().trim().isEmpty() ? 0 : Integer.parseInt(secondsInput.getText().trim());
            if (mins < 0 || secs < 0 || secs >= 60) {
                throw new IllegalArgumentException("Minutes should be non-negative and seconds should be between 0 and 59.");
            }
            timerService.setUpTimer(mins, secs);
        } catch (NumberFormatException ex) {
            minutesInput.setText("");
            secondsInput.setText("");
            minutesInput.setPromptText("Invalid input! Enter a number.");
            secondsInput.setPromptText("Invalid input! Enter a number.");
        }
    }

    private static Button getSetTimeButton(TextField minutesInput, TextField secondsInput, TimerService timerService) {
        Button setTimeButton = new Button("Set");
        setTimeButton.setOnAction(e -> handleTimeSet(minutesInput, secondsInput, timerService));
        return setTimeButton;
    }

    private Label getParticipantHeader(Participant participant, ParticipantType participantName) {
        Label header = new Label();
        header.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(participantName)));
        header.getStyleClass().add("header");

        if (participant.getParticipantType() == ParticipantType.AKA) {
            header.setStyle(STYLE_BACKGROUND_AKA_COLOR + STYLE_TEXT_FILL + STYLE_BACKGROUND_RADIUS);
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            header.setStyle(STYLE_BACKGROUND_AO_COLOR + STYLE_TEXT_FILL + STYLE_BACKGROUND_RADIUS);
        }

        return header;
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
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA);
        HBox categoryPanel = createCategoryPanel();
        configureLayoutHGrow(participantAO, timerPanel, participantAKA);
        mainLayout.getChildren().addAll( participantAO, timerPanel, participantAKA);
        root.setTop(categoryPanel);
        root.setCenter(mainLayout);
        Button closeModeButton = new Button(CLOSE_MODE + modeName);
        closeModeButton.setVisible(currentModeStage != null); // Set initial visibility
        closeModeButton.setOnAction(e -> {
            closeAllStages();
        });
        closeModeButton.setAlignment(Pos.CENTER);
        timerPanel.getChildren().add(closeModeButton);
    }

    private void configureLayoutHGrow(Node... nodes) {
        for (Node node : nodes)
            HBox.setHgrow(node, Priority.ALWAYS);
    }

    private void introduceScene() {
        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource(STYLE_CSS).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Karate Match Scoreboard");
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


    private VBox createParticipantPanel(Participant participant, ParticipantType participantName) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("participant-panel");

        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, true);

        panel.getChildren().addAll(getParticipantHeader(participant, participantName));
        panel.getChildren().add(penaltyComponent.getComponent());
        return panel;
    }

    private VBox createTimerPanel() {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.getStyleClass().add("timer-panel");

        // Interval control buttons
        Button startIntervalButton = new Button("4x15 Start");
        startIntervalButton.setOnAction(e -> timerService.startIntervalTimer(timerService.periodProperty().get()));
        Button stopIntervalButton = new Button("4x15 stop");
        stopIntervalButton.setOnAction(e -> timerService.stopAllIntervalTimers());
        Button resetTimerIntervalButton = new Button("4 x 15 Reset");
        resetTimerIntervalButton.setOnAction(e -> timerService.resetInterval());

        Label timerIntervalLabel1 = new Label();
        timerIntervalLabel1.textProperty().bind(Bindings.format("00:%02d:%02d", timerService.intervalSecondsProperty1(), timerService.intervalMillisecondsProperty1()));

        Label timerIntervalLabel2 = new Label();
        timerIntervalLabel2.textProperty().bind(Bindings.format("00:%02d:%02d", timerService.intervalSecondsProperty2(), timerService.intervalMillisecondsProperty2()));

        Label timerIntervalLabel3 = new Label();
        timerIntervalLabel3.textProperty().bind(Bindings.format("00:%02d:%02d", timerService.intervalSecondsProperty3(), timerService.intervalMillisecondsProperty3()));

        Label timerIntervalLabel4 = new Label();
        timerIntervalLabel4.textProperty().bind(Bindings.format("00:%02d:%02d", timerService.intervalSecondsProperty4(), timerService.intervalMillisecondsProperty4()));

        // Organizing buttons into rows
        HBox startStopIntervalButtons = new HBox(10, startIntervalButton, stopIntervalButton);
        HBox resetIntervalButtons = new HBox(10, resetTimerIntervalButton);

        VBox resetMiddle = new VBox(10);
        Button resetAll = new Button("Reset ALL");
        resetAll.setStyle("-fx-text-fill: orange");
        resetAll.setOnAction(e -> {
            timerService.resetInterval();
            timerService.reset();
            penaltyService.resetPenalties();
            categoryService.resetCategoryInfo();

        });
        resetMiddle.setAlignment(Pos.CENTER);
        resetMiddle.getChildren().add(resetAll);

        VBox intervalTimersBottom = new VBox(10, timerIntervalLabel1, timerIntervalLabel2, timerIntervalLabel3, timerIntervalLabel4, startStopIntervalButtons, resetIntervalButtons);
        intervalTimersBottom.setPadding(new Insets(20));
        intervalTimersBottom.setAlignment(Pos.BOTTOM_CENTER);

        // Make the interval timers take the remaining space at the bottom
        VBox.setVgrow(intervalTimersBottom, Priority.ALWAYS);

        timerPanel.getChildren().addAll(resetMiddle, intervalTimersBottom);

        return timerPanel;
    }

}
