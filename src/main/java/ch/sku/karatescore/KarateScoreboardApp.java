package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.SenshuService;
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
    private final SenshuService senshuService = new SenshuService();

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
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        // Main layout setup
        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);  // Increased spacing

        // Dynamically resize panels based on screen size
        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO, senshuService);
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA, senshuService);

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
        primaryStage.show();

        MenuView menuView = new MenuView(aka, ao, timerService, scoreService, penaltyService, senshuService);
        menuView.show();  // Assuming MenuView is another stage/dialog
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantName, SenshuService senshuService) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("participant-panel");

        Label header = new Label();
        header.textProperty().bind(Bindings.createStringBinding(
                () -> participantName + " - Total Points: " + scoreService.getTotalScoreProperty(participantName).get(),
                scoreService.getScoreProperty(participantName, ScoreType.YUKO),
                scoreService.getScoreProperty(participantName, ScoreType.WAZARI),
                scoreService.getScoreProperty(participantName, ScoreType.IPPON)
        ));
        header.getStyleClass().add("header");
        panel.setAlignment(Pos.CENTER);  // Center align contents

        // Setting header background color based on participant type
        if (participant.getParticipantType() == ParticipantType.AKA) {
            header.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            header.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        }

        Label scoreYuko = new Label();
        scoreYuko.textProperty().bind(Bindings.createStringBinding(
                () -> "Yuko: " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO).get(),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO)
        ));

        Label scoreWazaAri = new Label();
        scoreWazaAri.textProperty().bind(Bindings.createStringBinding(
                () -> "Waza-ari: " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI).get(),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI)
        ));

        Label scoreIppon = new Label();
        scoreIppon.textProperty().bind(Bindings.createStringBinding(
                () -> "Ippon: " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON).get(),
                scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON)
        ));

        Label senshuLabel = new Label("●");
        senshuLabel.setStyle("-fx-font-size: 100px; -fx-text-fill: yellow;");
        senshuLabel.visibleProperty().bind(senshuService.getSenshuProperty(participant.getParticipantType()));

        Button toggleSenshuButton = new Button("Toggle Senshu");
        toggleSenshuButton.setOnAction(e -> senshuService.getSenshuProperty(participant.getParticipantType()).set(!senshuService.getSenshuProperty(participant.getParticipantType()).get()));

        panel.getChildren().addAll(header, senshuLabel, scoreYuko, scoreWazaAri, scoreIppon, toggleSenshuButton);
        addButtonControls(panel, participant);

        // Pass true to include buttons
        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, true);
        panel.getChildren().add(penaltyComponent.getComponent());

        return panel;
    }
    private void addButtonControls(VBox panel, Participant participant) {
        // Set up control for each score type with its respective label
        setupScoreControl(panel, participant, ScoreType.YUKO);
        setupScoreControl(panel, participant, ScoreType.WAZARI);
        setupScoreControl(panel, participant, ScoreType.IPPON);
    }

    private void setupScoreControl(VBox panel, Participant participant, ScoreType scoreType) {
        Button btnAdd = new Button("+ " + scoreType.name());
        Button btnRemove = new Button("- " + scoreType.name());

        btnAdd.setOnAction(e -> {
            scoreService.addScore(participant.getParticipantType(), scoreType);
        });
        btnRemove.setOnAction(e -> {
            scoreService.subtractScore(participant.getParticipantType(), scoreType);
        });

        HBox scoreControls = new HBox(5, btnAdd, btnRemove);
        panel.getChildren().add(scoreControls);
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


//        Button nextPeriodButton = new Button("Next Period");
//        nextPeriodButton.setOnAction(e -> timerService.nextPeriod());

        // Organizing buttons into rows
        HBox startStopButtons = new HBox(10, startTimerButton, stopTimerButton);
        HBox startStopIntervalButtons = new HBox(10, startIntervalButton, stopIntervalButton);
        HBox resetIntervalButtons = new HBox(10, resetTimerIntervalButton);
        HBox setResetButtons = new HBox(10, setTimeButton, resetTimerButton);
        HBox inputFields = new HBox(10, minutesInput, secondsInput);

        // Container for interval timers, top and bottom
        VBox timerTop = new VBox(10, timerLabel, inputFields, startStopButtons, setResetButtons);
        timerTop.setPadding(new Insets(20));

        VBox resetMiddle = new VBox(10);
        Button resetAll = new Button("Remettre à zéro TOUT");
        resetAll.setOnAction(e -> {
            timerService.resetInterval();
            timerService.reset();
            scoreService.resetScores();
            penaltyService.resetPenalties();

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


}
