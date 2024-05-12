package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.TimerService;
import ch.sku.karatescore.view.PromoKumiteView;
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
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Correct reference to CSS
        Button btnOpenScoreboard = new Button("Open Scoreboard on TV");
        btnOpenScoreboard.setOnAction(e -> {
            WKFView wkfView = new WKFView(aka, ao, timerService , scoreService, penaltyService);
            wkfView.show();
        });

        Button btnOpenPromoKumite = new Button("Open Promokumite");
        btnOpenPromoKumite.setOnAction(e -> {
            PromoKumiteView promoKumiteView = new PromoKumiteView(aka, ao, scoreService, penaltyService);
            promoKumiteView.show();
        });

        StackPane rootPane = new StackPane(btnOpenScoreboard);
        StackPane secondPane = new StackPane(btnOpenPromoKumite);


        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        // Create panels for AO (left), Timer (center), and AKA (right)
        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO);
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA);

        // Add to main layout with AO on the left, AKA on the right
        mainLayout.getChildren().addAll(rootPane,secondPane, participantAO, timerPanel, participantAKA);
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

        Label header = new Label(participantName + " - Total Points: " + scoreService.calculateTotalScore(participantName));
        header.getStyleClass().add("header");
        // Setting header background color based on participant type
        if (participant.getParticipantType() == ParticipantType.AKA) {
            header.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        } else if (participant.getParticipantType() == ParticipantType.AO) {
            header.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        }

        updateHeaderWithScore(header, participant);

        Label scoreYuko = new Label("Yuko: " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO));
        Label scoreWazaAri = new Label("Waza-ari: " +scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI));
        Label scoreIppon = new Label("Ippon: " +scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON));

        panel.getChildren().addAll(header, scoreYuko, scoreWazaAri, scoreIppon);

        addButtonControls(panel, participant, scoreYuko, scoreWazaAri, scoreIppon, header);


        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService);
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
            scoreService.addScore(participant.getParticipantType() ,scoreType);
            updateScoresAndUI(participant, scoreYuko, scoreWazaAri, scoreIppon, header);
        });
        btnRemove.setOnAction(e -> {
            scoreService.subtractScore(participant.getParticipantType(), scoreType);
            updateScoresAndUI(participant, scoreYuko, scoreWazaAri, scoreIppon, header);
        });

        HBox scoreControls = new HBox(5, btnAdd, btnRemove);
        panel.getChildren().add(scoreControls);
    }

    private void updateScoresAndUI(Participant participant, Label scoreYuko, Label scoreWazaAri, Label scoreIppon, Label header) {
        scoreYuko.setText("Yuko: " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.YUKO));
        scoreWazaAri.setText("Waza-ari: " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.WAZARI));
        scoreIppon.setText("Ippon: " + scoreService.getScoreProperty(participant.getParticipantType(), ScoreType.IPPON));
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
        timerLabel.setStyle("-fx-font-size: 20px;");

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

        // Organizing buttons into rows
        HBox startStopButtons = new HBox(10, startTimerButton, stopTimerButton);
        HBox setResetButtons = new HBox(10, setTimeButton, resetTimerButton);
        HBox inputFields = new HBox(10, minutesInput, secondsInput);

        // Adding all components to the timer panel
        timerPanel.getChildren().addAll(timerLabel, inputFields, startStopButtons, setResetButtons);
        return timerPanel;
    }


}
