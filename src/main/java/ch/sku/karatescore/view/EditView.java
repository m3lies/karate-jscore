package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.SenshuService;
import ch.sku.karatescore.services.TimerService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class EditView {
    @Getter
    private final Stage stage;
    private final BorderPane root = new BorderPane();

    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;
    private final ScoreService scoreService;
    private final PenaltyService penaltyService;
    private final SenshuService senshuService;
    private final String modeName;
    private Stage currentModeStage; // Reference to the current mode stage
    public EditView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService, Stage currentModeStage, String modeName) {
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.senshuService = senshuService;
        this.stage = new Stage();
        this.currentModeStage = currentModeStage; // Set the current mode stage
        this.modeName = modeName; // Set the name of the clicked button
        initializeUI();
    }

    public void initializeUI() {
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Ensure CSS is loaded

        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);
        mainLayout.setPadding(new Insets(10));

        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO, senshuService);
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA, senshuService);

        HBox.setHgrow(participantAO, Priority.ALWAYS);
        HBox.setHgrow(timerPanel, Priority.ALWAYS);
        HBox.setHgrow(participantAKA, Priority.ALWAYS);

        mainLayout.getChildren().addAll(participantAO, timerPanel, participantAKA);
        root.setCenter(mainLayout);

        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // Ensure CSS is loaded for Scene
        stage.setScene(scene);
        stage.setTitle("Karate Match Scoreboard");
        stage.setMaximized(true);
        stage.show();



        Button closeModeButton = new Button("Close " + modeName + " Mode");
        closeModeButton.setVisible(currentModeStage != null); // Set initial visibility
        closeModeButton.setOnAction(e -> {
            if (currentModeStage != null) {
                currentModeStage.close();
                currentModeStage = null;
            }
            closeModeButton.setVisible(false); // Hide the button when the mode is closed
        });
        closeModeButton.setAlignment(Pos.CENTER);
        timerPanel.getChildren().add(closeModeButton);

    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantName, SenshuService senshuService) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("participant-panel");

        Label header = new Label();
        header.textProperty().bind(Bindings.createStringBinding(
                () -> participantName + " - Total Points: " + scoreService.getTotalScoreProperty(participantName).get(),
                scoreService.getTotalScoreProperty(participantName)
        ));
        header.getStyleClass().add("header");
        panel.setAlignment(Pos.CENTER);

        if (participant.getParticipantType() == ParticipantType.AKA) {
            header.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 10px;");
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            header.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10;-fx-background-radius: 10px;");
        }

        Label scoreYuko = new Label();
        scoreYuko.textProperty().bind(Bindings.createStringBinding(
                () -> "Yuko(1) : " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO).get(),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO)
        ));

        Label scoreWazaAri = new Label();
        scoreWazaAri.textProperty().bind(Bindings.createStringBinding(
                () -> "Waza-ari(2) : " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI).get(),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI)
        ));

        Label scoreIppon = new Label();
        scoreIppon.textProperty().bind(Bindings.createStringBinding(
                () -> "Ippon(3) : " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON).get(),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON)
        ));

        HBox scoreControls = new HBox(10, scoreYuko, scoreWazaAri, scoreIppon);
        scoreControls.setAlignment(Pos.CENTER);

        Button toggleSenshuButton = new Button("Senshu");
        toggleSenshuButton.setOnAction(e -> senshuService.getSenshuProperty(participant.getParticipantType()).set(!senshuService.getSenshuProperty(participant.getParticipantType()).get()));
        toggleSenshuButton.setStyle("-fx-font-size: 15px;");

        Label senshuLabel = new Label("● Senshu");
        senshuLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: orange;");
        senshuLabel.visibleProperty().bind(senshuService.getSenshuProperty(participant.getParticipantType()));

        HBox senshuBox = new HBox(10, toggleSenshuButton, senshuLabel);
        senshuBox.setAlignment(Pos.CENTER);
        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, true);

        panel.getChildren().addAll(header, scoreControls, senshuBox);
        addButtonControls(panel, participant);
        panel.getChildren().add(penaltyComponent.getComponent());
        return panel;
    }

    private void addButtonControls(VBox panel, Participant participant) {
        setupScoreControl(panel, participant, ScoreType.YUKO);
        setupScoreControl(panel, participant, ScoreType.WAZARI);
        setupScoreControl(panel, participant, ScoreType.IPPON);
    }

    private void setupScoreControl(VBox panel, Participant participant, ScoreType scoreType) {
        Button btnAdd = new Button("+ " + scoreType.name());
        Button btnRemove = new Button("- " + scoreType.name());

        btnAdd.setOnAction(e -> scoreService.addScore(participant.getParticipantType(), scoreType));
        btnRemove.setOnAction(e -> scoreService.subtractScore(participant.getParticipantType(), scoreType));

        HBox scoreControls = new HBox(5, btnAdd, btnRemove);
        scoreControls.setAlignment(Pos.CENTER);
        panel.getChildren().add(scoreControls);
    }

    private VBox createTimerPanel() {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.getStyleClass().add("timer-panel");

        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d:%02d", timerService.minutesProperty(), timerService.secondsProperty(), timerService.millisecondsProperty()));

        // User input fields for minutes and seconds
        TextField minutesInput = new TextField();
        TextField secondsInput = new TextField();
        minutesInput.setPromptText("minutes");
        secondsInput.setPromptText("seconds");
        // Setting preferred size for text fields
        minutesInput.setPrefWidth(80);
        secondsInput.setPrefWidth(80);

        // Timer control buttons
        Button setTimeButton = getSetTimeButton(minutesInput, secondsInput, timerService);

        // Start and Stop buttons
        Button startTimerButton = new Button("Start Timer");
        startTimerButton.setOnAction(e -> timerService.start());
        Button stopTimerButton = new Button("Stop Timer");
        stopTimerButton.setOnAction(e -> timerService.stop());

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
        HBox startStopButtons = new HBox(10, startTimerButton, stopTimerButton);
        startStopButtons.setAlignment(Pos.CENTER);
        HBox startStopIntervalButtons = new HBox(10, startIntervalButton, stopIntervalButton);
        HBox resetIntervalButtons = new HBox(10, resetTimerIntervalButton);

        HBox inputFieldsSetTimeButton = new HBox(5, minutesInput, secondsInput, setTimeButton);
        inputFieldsSetTimeButton.setAlignment(Pos.CENTER);

        // Container for interval timers, top and bottom
        VBox timerTop = new VBox(10, inputFieldsSetTimeButton, timerLabel, startStopButtons);
        timerTop.setAlignment(Pos.CENTER);
        timerTop.setPadding(new Insets(20));

        VBox resetMiddle = new VBox(10);
        Button resetAll = new Button("Reset ALL");
        resetAll.setStyle("-fx-text-fill: orange");
        resetAll.setOnAction(e -> {
            timerService.resetInterval();
            timerService.reset();
            scoreService.resetScores();
            penaltyService.resetPenalties();
            senshuService.resetSenshus();
        });
        resetMiddle.setAlignment(Pos.CENTER);
        resetMiddle.getChildren().add(resetAll);

        VBox intervalTimersBottom = new VBox(10, timerIntervalLabel1, timerIntervalLabel2, timerIntervalLabel3, timerIntervalLabel4, startStopIntervalButtons, resetIntervalButtons);
        intervalTimersBottom.setPadding(new Insets(20));
        intervalTimersBottom.setAlignment(Pos.BOTTOM_CENTER);

        // Make the interval timers take the remaining space at the bottom
        VBox.setVgrow(intervalTimersBottom, Priority.ALWAYS);

        timerPanel.getChildren().addAll(timerTop, resetMiddle, intervalTimersBottom);

        return timerPanel;
    }

    private static Button getSetTimeButton(TextField minutesInput, TextField secondsInput, TimerService timerService) {
        Button setTimeButton = new Button("Set");

        setTimeButton.setOnAction(e -> {
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
        });

        return setTimeButton;
    }


}
