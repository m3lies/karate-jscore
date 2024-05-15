package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.components.PenaltyComponent;
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

        akaPanel.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
        aoPanel.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);

        root.getChildren().addAll(akaPanel, aoPanel);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        setFullScreen();
        stage.setTitle("Karate Match Scoreboard");
    }

    private VBox createParticipantPanel(Participant participant, ParticipantType type) {
        VBox panel = new VBox(10);
        String backgroundColor = type == ParticipantType.AKA ? "#dc3545" : "#007bff";  // Red for AKA, Blue for AO
        panel.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white;");
        panel.setAlignment(Pos.CENTER);

        Label timerIntervalLabel1 = new Label();
        Label timerIntervalLabel2 = new Label();
        Label timerIntervalLabel3 = new Label();
        Label timerIntervalLabel4 = new Label();

        double halfScreenWidth = Screen.getPrimary().getVisualBounds().getWidth() / 2;

        timerIntervalLabel1.setPrefWidth(halfScreenWidth);
        timerIntervalLabel2.setPrefWidth(halfScreenWidth);
        timerIntervalLabel3.setPrefWidth(halfScreenWidth);
        timerIntervalLabel4.setPrefWidth(halfScreenWidth);

        timerIntervalLabel1.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");
        timerIntervalLabel2.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");
        timerIntervalLabel3.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");
        timerIntervalLabel4.setStyle("-fx-font-size: 34px; -fx-text-fill: white;");

        if (type == ParticipantType.AKA) {
            timerIntervalLabel1.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty1()));
            timerIntervalLabel3.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty3()));
        } else {
            timerIntervalLabel2.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty2()));
            timerIntervalLabel4.textProperty().bind(Bindings.format("00:%02d", timerService.intervalSecondsProperty4()));
        }

        VBox intervalLabelsBox = new VBox(10, timerIntervalLabel1, timerIntervalLabel2, timerIntervalLabel3, timerIntervalLabel4);
        intervalLabelsBox.setAlignment(Pos.CENTER);

        PenaltyComponent penaltyComponent = new PenaltyComponent(participant, penaltyService, false);

        panel.getChildren().addAll(intervalLabelsBox, penaltyComponent.getComponent());

        return panel;
    }

    private void setFullScreen() {
        Screen screen = Screen.getPrimary();
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
