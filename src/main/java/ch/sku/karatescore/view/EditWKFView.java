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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.Objects;

public class EditWKFView {
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
    private final ScoreService scoreService;
    private final PenaltyService penaltyService;
    private final SenshuService senshuService;
    private final CategoryService categoryService;
    private final String modeName;
    private Stage currentModeStage;// Reference to the current mode stage

    private boolean timerRunning = false;

    public EditWKFView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService, CategoryService categoryService, Stage currentModeStage, String modeName) {
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.senshuService = senshuService;
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
        Scene scene = introduceScene();
        setupMainLayout(scene);
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

    private void setupMainLayout(Scene scene) {
        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);
        mainLayout.setPadding(new Insets(10));
        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO, senshuService);
        VBox timerPanel = createTimerPanel(scene);
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA, senshuService);
        HBox categoryPanel = createCategoryPanel();
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
        stage.setTitle("WKF Mode");
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
        setupScoreControl(panel, participant, ScoreType.IPPON);
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

    private VBox createParticipantPanel(Participant participant, ParticipantType participantName, SenshuService senshuService) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.getStyleClass().add("participant-panel");

        // --- Header (AO / AKA + Total Points)
        Label header = getParticipantHeader(participant, participantName);

        // --- Score labels row (Yuko, Wazari, Ippon)
        HBox scoreControls = new HBox(30,
                getScoreLabel(participant, ScoreType.YUKO),
                getScoreLabel(participant, ScoreType.WAZARI),
                getScoreLabel(participant, ScoreType.IPPON)
        );
        scoreControls.setAlignment(Pos.CENTER);

        // --- Senshu button + indicator
        Button toggleSenshuButton = new Button("Senshu");
        toggleSenshuButton.setOnAction(e ->
                senshuService.getSenshuProperty(participant.getParticipantType())
                        .set(!senshuService.getSenshuProperty(participant.getParticipantType()).get())
        );
        toggleSenshuButton.setStyle("-fx-font-size: 15px; -fx-pref-width: 100px;");

        Label senshuLabel = new Label("● Senshu");
        senshuLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: orange;");
        senshuLabel.visibleProperty().bind(senshuService.getSenshuProperty(participant.getParticipantType()));

        HBox senshuBox = new HBox(10, toggleSenshuButton, senshuLabel);
        senshuBox.setAlignment(Pos.CENTER);

        // --- Score control buttons (+/- Yuko, Wazari, Ippon)
        VBox scoreButtonsBox = new VBox(10);
        scoreButtonsBox.setAlignment(Pos.CENTER);
        addButtonControls(scoreButtonsBox, participant);

        // --- Penalty section
        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, true);

        // --- Combine everything neatly
        panel.getChildren().addAll(
                header,
                scoreControls,
                senshuBox,
                scoreButtonsBox,
                penaltyComponent.getComponent()
        );

        return panel;
    }

    private VBox createTimerPanel(Scene scene) {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.setAlignment(Pos.CENTER);
        timerPanel.getStyleClass().add("timer-panel");

        // Timer label
        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d:%02d", timerService.minutesProperty(), timerService.secondsProperty(), timerService.millisecondsProperty()));

        // Input fields
        TextField minutesInput = new TextField();
        TextField secondsInput = new TextField();
        minutesInput.setPromptText("min");
        secondsInput.setPromptText("sec");
        minutesInput.setPrefWidth(60);
        secondsInput.setPrefWidth(60);

        Button setTimeButton = getSetTimeButton(minutesInput, secondsInput, timerService);

        // Start/Stop
        Button startTimerButton = new Button("Start");
        startTimerButton.setOnAction(e -> {
            timerService.start();
            timerRunning = true;
        });

        Button stopTimerButton = new Button("Stop");
        stopTimerButton.setOnAction(e -> {
            timerService.stop();
            timerRunning = false;
        });

        // Arrange inputs and controls
        HBox inputRow = new HBox(10, minutesInput, secondsInput, setTimeButton);
        inputRow.setAlignment(Pos.CENTER);

        HBox startStopRow = new HBox(15, startTimerButton, stopTimerButton);
        startStopRow.setAlignment(Pos.CENTER);

        VBox controlsBox = new VBox(15, inputRow, timerLabel, startStopRow);
        controlsBox.setAlignment(Pos.CENTER);

        // Spacer to push bottom buttons down
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Reset and Close buttons (bottom bar)
        Button resetAll = new Button("Reset ALL");
        resetAll.setStyle("-fx-text-fill: orange;");
        resetAll.setOnAction(e -> {
            timerService.resetInterval();
            timerService.reset();
            scoreService.resetScores();
            penaltyService.resetPenalties();
            senshuService.resetSenshus();
            categoryService.resetCategoryInfo();
        });

        Button closeModeButton = new Button(CLOSE_MODE + modeName);
        closeModeButton.setVisible(currentModeStage != null);

        closeModeButton.setOnAction(e -> closeAllStages());

        HBox bottomButtons = new HBox(15, closeModeButton, resetAll);
        bottomButtons.setAlignment(Pos.CENTER);

        // Final layout
        timerPanel.getChildren().addAll(controlsBox, spacer, bottomButtons);

        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (timerRunning) {
                    timerService.stop();
                } else {
                    timerService.start();
                }
                timerRunning = !timerRunning;
                event.consume(); // optional — stops space from clicking buttons
            }
        });


        return timerPanel;
    }


}
