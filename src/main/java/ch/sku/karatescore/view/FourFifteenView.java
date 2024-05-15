package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.TimerService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class FourFifteenView {
    private final Stage stage;
    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;
    private final PenaltyService penaltyService;

    public FourFifteenView(Participant aka, Participant ao, TimerService timerService, PenaltyService penaltyService) {
        this.stage = new Stage();
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.penaltyService = penaltyService;
        initializeUI();
    }

    private void initializeUI() {
        HBox root = new HBox();

        VBox akaPanel = createParticipantPanel(aka, ParticipantType.AKA);
        VBox aoPanel = createParticipantPanel(ao, ParticipantType.AO);

        HBox.setHgrow(akaPanel, Priority.ALWAYS);
        HBox.setHgrow(aoPanel, Priority.ALWAYS);
        akaPanel.setMaxWidth(Double.MAX_VALUE);
        aoPanel.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(akaPanel, aoPanel);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        setFullScreen();
        stage.setTitle("Karate Match Scoreboard");
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType type) {
        VBox panel = new VBox(10);
        String backgroundColor = type == ParticipantType.AKA ?  "#dc3545" : "#007bff";  // Red for AKA, Blue for AO
        panel.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white;");
        panel.setAlignment(Pos.CENTER);

        // Timer label specific to the participant

        Label timerIntervalLabel1 = new Label();
        Label timerIntervalLabel2 = new Label();
        Label timerIntervalLabel3 = new Label();
        Label timerIntervalLabel4 = new Label();
//        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
//        double halfScreenWidth = screenWidth / 2;
//
//        timerIntervalLabel1.setPrefWidth(halfScreenWidth);
//        timerIntervalLabel2.setPrefWidth(halfScreenWidth);
//        timerIntervalLabel3.setPrefWidth(halfScreenWidth);
//        timerIntervalLabel4.setPrefWidth(halfScreenWidth);

        timerIntervalLabel1.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");
        timerIntervalLabel2.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");
        timerIntervalLabel3.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");
        timerIntervalLabel4.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");


        // Bind the timer label text and period label text to the appropriate timer service properties
        if (type == ParticipantType.AKA) {
            timerIntervalLabel1.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty1()));
            timerIntervalLabel3.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty3()));

        } else {
            timerIntervalLabel2.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty2()));
            timerIntervalLabel4.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty4()));


        }



        // Add the timer and period labels to the panel
        panel.getChildren().addAll(timerIntervalLabel1, timerIntervalLabel3,  timerIntervalLabel2,timerIntervalLabel4);
        // Add penalty labels
        addPenaltyLabels(participant, panel);

        return panel;
    }

    private void addPenaltyLabels(Participant participant, VBox panel) {
        HBox penaltyContainer = new HBox(10); // Create an HBox with spacing
        penaltyContainer.setAlignment(Pos.CENTER); // Center the penalties horizontally

        for (PenaltyType penaltyType : PenaltyType.values()) {
            Label penaltyLabel = new Label(penaltyType.toString());
            penaltyLabel.getStyleClass().add("penalty-label");
            penaltyLabel.getStyleClass().add("button");
            penaltyLabel.setStyle("-fx-font-size: 15px;");// Applying styles

            // Bind the visible property to whether the penalty is given or not
            penaltyLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participant.getParticipantType(), penaltyType));
            penaltyLabel.managedProperty().bind(penaltyLabel.visibleProperty());

            // Bind opacity for smooth visual transitions
            penaltyLabel.opacityProperty().bind(Bindings.when(penaltyService.getPenaltyProperty(participant.getParticipantType(), penaltyType)).then(1.0).otherwise(0.0));

            penaltyContainer.getChildren().add(penaltyLabel);
        }

        panel.getChildren().add(penaltyContainer);
    }


    private void setFullScreen() {
        Screen screen = Screen.getPrimary();  // Adjust this if multi-screen setup
        stage.setX(screen.getVisualBounds().getMinX());
        stage.setY(screen.getVisualBounds().getMinY());
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setFullScreen(true);
    }

    public void show() {
        stage.show();
    }

}
