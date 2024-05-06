package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import ch.sku.karatescore.components.TimerComponent;
import ch.sku.karatescore.model.Participant;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class KarateScoreboardApp extends Application {
    private final Participant aka = new Participant(ParticipantType.AKA);
    private final Participant ao = new Participant(ParticipantType.AO);
    private final BorderPane root = new BorderPane();

    private final Label scoreDisplayAKA = new Label();
    private final Label scoreDisplayAO = new Label();
    private final Label penaltyDisplayAKA = new Label();
    private final Label penaltyDisplayAO = new Label();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Buttons to switch views
        Button btnFullView = new Button("WKF mode");
        Button btnNoIpponView = new Button("Promo Kumite mode");
        Button btnNoPointsView = new Button("4 x 15 mode");

        btnFullView.setOnAction(e -> switchView(createWKFView()));
        btnNoIpponView.setOnAction(e -> switchView(createPromoKumiteView()));
        btnNoPointsView.setOnAction(e -> switchView(createFourFifteenView()));

        HBox menu = new HBox(10, btnFullView, btnNoIpponView, btnNoPointsView);
        root.setTop(menu);

        // Start with the full view
        switchView(createWKFView());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Karate Match");
        primaryStage.show();
    }

    private void switchView(VBox newView) {
        root.setCenter(newView);  // Replace the center of BorderPane with the new view
    }

    private VBox createWKFView() {
        // Main layout VBox
        VBox layout = new VBox(10);

        // HBox to hold AKA and AO info
        HBox infoHBox = new HBox(10);

        // VBox for AKA info and controls
        VBox akaBox = new VBox(10);
        VBox akaInfo = new VBox(5, new Label("Participant AKA"), scoreDisplayAKA, penaltyDisplayAKA);
        HBox akaControls = new HBox(10);
        addScoreControls(akaControls, aka, "AKA", ScoreType.YUKO, ScoreType.WAZARI, ScoreType.IPPON);
        akaBox.getChildren().addAll(akaInfo, akaControls);

        // VBox for AO info and controls
        VBox aoBox = new VBox(10);
        VBox aoInfo = new VBox(5, new Label("Participant AO"), scoreDisplayAO, penaltyDisplayAO);
        HBox aoControls = new HBox(10);
        addScoreControls(aoControls, ao, "AO", ScoreType.YUKO, ScoreType.WAZARI, ScoreType.IPPON);
        aoBox.getChildren().addAll(aoInfo, aoControls);

        // Add AKA and AO VBox containers to the HBox
        infoHBox.getChildren().addAll(akaBox, aoBox);

        // Create TimerComponent instance
        TimerComponent timerComponent = new TimerComponent();
        VBox timerBox = new VBox(timerComponent);  // Add TimerComponent to a VBox

        // Add infoHBox and timerBox to layout
        layout.getChildren().addAll(infoHBox, timerBox);

        // Update displays to show initial values
        updateDisplays();

        return layout;
    }

    private VBox createPromoKumiteView() {
        VBox layout = new VBox(10);
        return layout;
    }

    private VBox createFourFifteenView() {
        VBox layout = new VBox(10);
        // Possibly add penalty controls or other relevant components
        return layout;
    }

    private void addScoreControls(HBox hbox, Participant participant, String labelPrefix, ScoreType... scoreTypes) {
        for (ScoreType scoreType : scoreTypes) {
            Button btnAddScore = new Button("Add " + scoreType + " " + labelPrefix);
            btnAddScore.setOnAction(e -> {
                participant.addScore(scoreType);
                updateDisplays(); // Update displays immediately after score adjustment
            });

            Button btnSubtractScore = new Button("Subtract " + scoreType + " " + labelPrefix);
            btnSubtractScore.setOnAction(e -> {
                participant.subtractScore(scoreType);
                updateDisplays(); // Update displays immediately after score adjustment
            });

            hbox.getChildren().addAll(btnAddScore, btnSubtractScore);
        }
    }

    private void updateDisplays() {
        // Calculate total score and count of each score type for AKA
        int totalScoreAKA = aka.calculateTotalScore();
        int yukoCountAKA = aka.getScores().getOrDefault(ScoreType.YUKO, 0);
        int wazaAriCountAKA = aka.getScores().getOrDefault(ScoreType.WAZARI, 0);
        int ipponCountAKA = aka.getScores().getOrDefault(ScoreType.IPPON, 0);

        // Calculate total score and count of each score type for AO
        int totalScoreAO = ao.calculateTotalScore();
        int yukoCountAO = ao.getScores().getOrDefault(ScoreType.YUKO, 0);
        int wazaAriCountAO = ao.getScores().getOrDefault(ScoreType.WAZARI, 0);
        int ipponCountAO = ao.getScores().getOrDefault(ScoreType.IPPON, 0);

        // Update labels with the calculated values
        scoreDisplayAKA.setText(String.format("Scores AKA: %d (Yuko: %d, Waza-ari: %d, Ippon: %d)", totalScoreAKA, yukoCountAKA, wazaAriCountAKA, ipponCountAKA));
        scoreDisplayAO.setText(String.format("Scores AO: %d (Yuko: %d, Waza-ari: %d, Ippon: %d)", totalScoreAO, yukoCountAO, wazaAriCountAO, ipponCountAO));
        penaltyDisplayAKA.setText("Penalties AKA: " + aka.getPenalties().toString());
        penaltyDisplayAO.setText("Penalties AO: " + ao.getPenalties().toString());
    }
}
