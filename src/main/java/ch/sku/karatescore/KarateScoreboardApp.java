package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.SenshuService;
import ch.sku.karatescore.services.TimerService;
import ch.sku.karatescore.view.MenuView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class KarateScoreboardApp extends Application {
    private final Participant aka = new Participant(ParticipantType.AKA);
    private final Participant ao = new Participant(ParticipantType.AO);

    private final TimerService timerService = new TimerService();
    private final ScoreService scoreService = new ScoreService();
    private final PenaltyService penaltyService = new PenaltyService();
    private final SenshuService senshuService = new SenshuService();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MenuView menuView = MenuView.getInstance(aka, ao, timerService, scoreService, penaltyService, senshuService);
        menuView.show();

        // Set up a close request handler for the MenuView stage to ensure the application exits
        menuView.getStage().setOnCloseRequest(event -> {
            Platform.exit(); // Close the entire application
        });
    }
}
