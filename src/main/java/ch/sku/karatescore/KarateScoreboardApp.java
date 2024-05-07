package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.model.MatchData;
import ch.sku.karatescore.model.Participant;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class KarateScoreboardApp extends Application {
    private final BorderPane root = new BorderPane();
    private final Participant aka = new Participant(ParticipantType.AKA);
    private final Participant ao = new Participant(ParticipantType.AO);
    private final MatchData matchData = new MatchData();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Correct reference to CSS

        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        // Create panels for AO (left), Timer (center), and AKA (right)
        VBox participantAO = createParticipantPanel(ao, ParticipantType.AO);
        VBox timerPanel = createTimerPanel();
        VBox participantAKA = createParticipantPanel(aka, ParticipantType.AKA);

        // Add to main layout with AO on the left, AKA on the right
        mainLayout.getChildren().addAll(participantAO, timerPanel, participantAKA);
        root.setCenter(mainLayout);

        Scene scene = new Scene(root, 1024, 768);
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

        updateHeaderWithScore(header, participant);

        Label scoreYuko = new Label("Yuko: " + participant.getScores().getOrDefault(ScoreType.YUKO, 0));
        Label scoreWazaAri = new Label("Waza-ari: " + participant.getScores().getOrDefault(ScoreType.WAZARI, 0));
        Label scoreIppon = new Label("Ippon: " + participant.getScores().getOrDefault(ScoreType.IPPON, 0));

        panel.getChildren().addAll(header, scoreYuko, scoreWazaAri, scoreIppon);

        addButtonControls(panel, participant, scoreYuko, ScoreType.YUKO);
        addButtonControls(panel, participant, scoreWazaAri, ScoreType.WAZARI);
        addButtonControls(panel, participant, scoreIppon, ScoreType.IPPON);

        PenaltyComponent penaltyComponent = new PenaltyComponent(participant);
        panel.getChildren().add(penaltyComponent.getComponent());
        return panel;
    }

    private void addButtonControls(VBox panel, Participant participant, Label scoreLabel, ScoreType scoreType) {
        Button btnAdd = new Button("+ " + scoreType.name());
        Button btnRemove = new Button("- " + scoreType.name());

        btnAdd.setOnAction(e -> {
            participant.addScore(scoreType);
            scoreLabel.setText(scoreType.name() + ": " + participant.getScores().getOrDefault(scoreType, 0));
            updateHeaderWithScore((Label) panel.getChildren().get(0), participant);
        });
        btnRemove.setOnAction(e -> {
            participant.subtractScore(scoreType);
            scoreLabel.setText(scoreType.name() + ": " + participant.getScores().getOrDefault(scoreType, 0));
            updateHeaderWithScore((Label) panel.getChildren().get(0), participant);
        });

        HBox scoreControls = new HBox(5, btnAdd, btnRemove);
        panel.getChildren().add(scoreControls);
    }

    private void updateHeaderWithScore(Label header, Participant participant) {
        int totalScore = participant.calculateTotalScore();
        header.setText(participant.getParticipantType() + " - Total Points: " + totalScore);
    }

    private VBox createTimerPanel() {
        VBox timerPanel = new VBox(10);
        timerPanel.setPadding(new Insets(20));
        timerPanel.getStyleClass().add("timer-panel");

        Label timerLabel = new Label("Timer: 00:00");
        Button resetTimerButton = new Button("Reset Timer");
        timerPanel.getChildren().addAll(timerLabel, resetTimerButton);

        return timerPanel;
    }
}
