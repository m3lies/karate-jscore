package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.TimerService;
import ch.sku.karatescore.view.MenuView;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

public class KarateScoreboardApp extends Application {
    private final BorderPane root = new BorderPane();
    private final Participant aka = new Participant(ParticipantType.AKA);
    private final Participant ao = new Participant(ParticipantType.AO);

    private final TimerService timerService = new TimerService();
    private final ScoreService scoreService = new ScoreService();
    private final PenaltyService penaltyService = new PenaltyService();


    public static void main(String[] args) {
        launch(args);
    }

    private static Button getSetTimeButton(TextField minutesInput, TextField secondsInput, TimerService timerService) {
        Button setTimeButton = new Button("Set Timer");

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

    @Override
    public void start(Stage primaryStage) {
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Ensure CSS is loaded

        // Main layout setup
        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);  // Increased spacing

        // Dynamically resize panels based on screen size
        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO);
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA);

        // Setting HGrow to always for expanding automatically
        HBox.setHgrow(participantAO, Priority.ALWAYS);
        HBox.setHgrow(timerPanel, Priority.ALWAYS);
        HBox.setHgrow(participantAKA, Priority.ALWAYS);

        // Adding panels to the layout
        mainLayout.getChildren().addAll(participantAO, timerPanel, participantAKA);
        root.setCenter(mainLayout);

        Scene scene = new Scene(root, 1920, 1080);  // Set initial size but it can be maximized
        primaryStage.setScene(scene);
        primaryStage.setTitle("Karate Match Scoreboard");
        primaryStage.setMaximized(true);  // Start maximized
        adjustFontSize(primaryStage, root);
        primaryStage.show();

        MenuView menuView = new MenuView(aka, ao, timerService, scoreService, penaltyService);
        menuView.show();  // Assuming MenuView is another stage/dialog
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantName) {

        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("participant-panel");

        Label header = new Label();
        header.setText(participantName + " - Total Points: " + scoreService.calculateTotalScore(participantName));
        header.getStyleClass().add("header");
        panel.setAlignment(Pos.CENTER);  // Center align contents

        // Setting header background color based on participant type
        if (participant.getParticipantType() == ParticipantType.AKA) {
            header.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            header.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        }

        updateHeaderWithScore(header, participant);

        Label scoreYuko = new Label();
        scoreYuko.textProperty().bind(Bindings.format("Yuko: %d", scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO)));

        Label scoreWazaAri = new Label();
        scoreWazaAri.textProperty().bind(Bindings.format("Waza-ari: %d", scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI)));

        Label scoreIppon = new Label();
        scoreIppon.textProperty().bind(Bindings.format("Ippon: %d", scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON)));

        panel.getChildren().addAll(header, scoreYuko, scoreWazaAri, scoreIppon);

        addButtonControls(panel, participant, scoreYuko, scoreWazaAri, scoreIppon, header);

        // Pass true to include buttons
        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, true);
        panel.getChildren().add(penaltyComponent.getComponent());

        return panel;
    }

    private void adjustFontSize(Stage stage, Pane root) {
        DoubleBinding fontSizeBinding = Bindings.createDoubleBinding(() -> stage.getWidth() * 0.025, // 2.5% of the stage width as font size
                stage.widthProperty());

        root.styleProperty().bind(Bindings.createStringBinding(() -> String.format("-fx-font-size: %.2fpx;", fontSizeBinding.get()), fontSizeBinding));
    }

    private void addButtonControls(VBox panel, Participant participant, Label scoreYuko, Label scoreWazaAri, Label scoreIppon, Label header) {
        // Set up control for each score type with its respective label
        setupScoreControl(panel, participant, ScoreType.YUKO, header, scoreYuko, scoreWazaAri, scoreIppon);
        setupScoreControl(panel, participant, ScoreType.WAZARI, header, scoreYuko, scoreWazaAri, scoreIppon);
        setupScoreControl(panel, participant, ScoreType.IPPON, header, scoreYuko, scoreWazaAri, scoreIppon);
    }

    private void setupScoreControl(VBox panel, Participant participant, ScoreType scoreType, Label header, Label scoreYuko, Label scoreWazaAri, Label scoreIppon) {
        Button btnAdd = new Button("+ " + scoreType.name());
        Button btnRemove = new Button("- " + scoreType.name());

        btnAdd.setOnAction(e -> {
            scoreService.addScore(participant.getParticipantType(), scoreType);
            updateScoresAndUI(participant, header);
        });
        btnRemove.setOnAction(e -> {
            scoreService.subtractScore(participant.getParticipantType(), scoreType);
            updateScoresAndUI(participant, header);
        });

        HBox scoreControls = new HBox(5, btnAdd, btnRemove);
        panel.getChildren().add(scoreControls);
    }

    private void updateScoresAndUI(Participant participant, Label header) {
        updateHeaderWithScore(header, participant);
    }

    private void updateHeaderWithScore(Label header, Participant participant) {
        int totalScore = scoreService.calculateTotalScore(participant.getParticipantType());
        header.setText(participant.getParticipantType() + " - Total Points: " + totalScore);
    }

    private VBox createTimerPanel() {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.getStyleClass().add("timer-panel");

        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d", timerService.minutesProperty(), timerService.secondsProperty()));

        // User input fields for minutes and seconds
        TextField minutesInput = new TextField();
        TextField secondsInput = new TextField();
        minutesInput.setPromptText("Enter minutes");
        secondsInput.setPromptText("Enter seconds");

        // Timer control buttons
        Button setTimeButton = getSetTimeButton(minutesInput, secondsInput, timerService);
        Button resetTimerButton = new Button("Reset Timer");
        resetTimerButton.setOnAction(e -> timerService.reset());

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
        timerIntervalLabel1.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty1()));


        Label timerIntervalLabel2 = new Label();
        timerIntervalLabel2.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty2()));


        Label timerIntervalLabel3 = new Label();
        timerIntervalLabel3.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty3()));


        Label timerIntervalLabel4 = new Label();
        timerIntervalLabel4.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty4()));


        Button nextPeriodButton = new Button("Next Period");
        nextPeriodButton.setOnAction(e -> timerService.nextPeriod());

        // Organizing buttons into rows
        HBox startStopButtons = new HBox(10, startTimerButton, stopTimerButton);
        HBox startStopIntervalButtons = new HBox(10, startIntervalButton, stopIntervalButton, nextPeriodButton);
        HBox resetIntervalButtons = new HBox(10, resetTimerIntervalButton);
        HBox setResetButtons = new HBox(10, setTimeButton, resetTimerButton);
        HBox inputFields = new HBox(10, minutesInput, secondsInput);

        // Container for interval timers, top and bottom
        VBox timerTop = new VBox(10, timerLabel, inputFields, startStopButtons, setResetButtons);
        timerTop.setPadding(new Insets(20));

        VBox intervalTimersBottom = new VBox(10, timerIntervalLabel1, timerIntervalLabel2, timerIntervalLabel3, timerIntervalLabel4, startStopIntervalButtons, resetIntervalButtons);
        intervalTimersBottom.setPadding(new Insets(20));
        intervalTimersBottom.setAlignment(Pos.BOTTOM_CENTER);

        // Make the interval timers take the remaining space at the bottom
        VBox.setVgrow(intervalTimersBottom, Priority.ALWAYS);

        timerPanel.getChildren().addAll(timerTop, intervalTimersBottom);

        return timerPanel;
    }


}
