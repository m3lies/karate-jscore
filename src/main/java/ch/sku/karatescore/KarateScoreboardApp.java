package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.TimerService;
import ch.sku.karatescore.view.WKFView;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class KarateScoreboardApp extends Application {
    private final BorderPane root = new BorderPane();
    private final Participant aka = new Participant(ParticipantType.AKA);
    private final Participant ao = new Participant(ParticipantType.AO);

    private final TimerService timerService = new TimerService();

    public static void main(String[] args) {
        launch(args);
    }

    private static Button getSetTimeButton(TextField minutesInput, TextField secondsInput, TimerService timerService) {
        Button setTimeButton = new Button("Set Timer");

        setTimeButton.setOnAction(e -> {
            try {
                int mins = minutesInput.getText().trim().isEmpty() ? 0 : Integer.parseInt(minutesInput.getText().trim());
                int secs = secondsInput.getText().trim().isEmpty() ? 0 : Integer.parseInt(secondsInput.getText().trim());

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
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Correct reference to CSS
        Button btnOpenScoreboard = new Button("Open Scoreboard on TV");
        btnOpenScoreboard.setOnAction(e -> {
            WKFView wkfView = new WKFView(aka, ao, timerService);
            wkfView.show();
        });

        StackPane rootPane = new StackPane(btnOpenScoreboard);


        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        // Create panels for AO (left), Timer (center), and AKA (right)
        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO);
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA);

        // Add to main layout with AO on the left, AKA on the right
        mainLayout.getChildren().addAll(rootPane, participantAO, timerPanel, participantAKA);
        root.setCenter(mainLayout);

        Scene scene = new Scene(root, 1920, 1080);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Karate Match Scoreboard");
        primaryStage.show();
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType participantName) {

        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.getStyleClass().add("participant-panel");

        Label header = new Label(participantName + " - Total Points: " + participant.calculateTotalScore());
        header.getStyleClass().add("header");
        // Setting header background color based on participant type
        if (participant.getParticipantType() == ParticipantType.AKA) {
            header.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            header.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        }

        updateHeaderWithScore(header, participant);

        Label scoreYuko = new Label("Yuko: " + participant.getScores().getOrDefault(ScoreType.YUKO, 0));
        Label scoreWazaAri = new Label("Waza-ari: " + participant.getScores().getOrDefault(ScoreType.WAZARI, 0));
        Label scoreIppon = new Label("Ippon: " + participant.getScores().getOrDefault(ScoreType.IPPON, 0));

        panel.getChildren().addAll(header, scoreYuko, scoreWazaAri, scoreIppon);

        addButtonControls(panel, participant, scoreYuko, scoreWazaAri, scoreIppon, header);


        PenaltyComponent penaltyComponent = new PenaltyComponent(participant);
        panel.getChildren().add(penaltyComponent.getComponent());
        return panel;
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
            participant.addScore(scoreType);
            updateScoresAndUI(participant, scoreYuko, scoreWazaAri, scoreIppon, header);
        });
        btnRemove.setOnAction(e -> {
            participant.subtractScore(scoreType);
            updateScoresAndUI(participant, scoreYuko, scoreWazaAri, scoreIppon, header);
        });

        HBox scoreControls = new HBox(5, btnAdd, btnRemove);
        panel.getChildren().add(scoreControls);
    }

    private void updateScoresAndUI(Participant participant, Label scoreYuko, Label scoreWazaAri, Label scoreIppon, Label header) {
        scoreYuko.setText("Yuko: " + participant.getScoreCounts().getOrDefault(ScoreType.YUKO, 0));
        scoreWazaAri.setText("Waza-ari: " + participant.getScoreCounts().getOrDefault(ScoreType.WAZARI, 0));
        scoreIppon.setText("Ippon: " + participant.getScoreCounts().getOrDefault(ScoreType.IPPON, 0));
        updateHeaderWithScore(header, participant);
    }

    private void updateHeaderWithScore(Label header, Participant participant) {
        int totalScore = participant.calculateTotalScore();
        header.setText(participant.getParticipantType() + " - Total Points: " + totalScore);
    }

    private VBox createTimerPanel() {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.getStyleClass().add("timer-panel");

        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.format("%02d:%02d", timerService.minutesProperty(), timerService.secondsProperty()));
        timerLabel.setStyle("-fx-font-size: 20px;");

        // Setup for user input and timer control buttons
        TextField minutesInput = new TextField();
        TextField secondsInput = new TextField();
        Button setTimeButton = getSetTimeButton(minutesInput, secondsInput, timerService);
        Button startTimerButton = new Button("Start Timer");
        startTimerButton.setOnAction(e -> timerService.start());
        Button stopTimerButton = new Button("Stop Timer");
        stopTimerButton.setOnAction(e -> timerService.stop());
        Button resetTimerButton = new Button("Reset Timer");
        resetTimerButton.setOnAction(e -> timerService.reset());  // Resets to 00:00

        timerPanel.getChildren().addAll(timerLabel, minutesInput, secondsInput, setTimeButton, startTimerButton, stopTimerButton, resetTimerButton);
        return timerPanel;
    }


}
