package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.components.ScoreComponent;
import ch.sku.karatescore.components.TimerComponent;
import ch.sku.karatescore.model.MatchData;
import ch.sku.karatescore.view.MatchDataView;
import ch.sku.karatescore.view.PromoKumiteView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;


public class KarateScoreboardApp extends Application {
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Karate Scoreboard");
        MatchDataView matchDataView =new MatchDataView();
        PromoKumiteView promoKumiteView = new PromoKumiteView();
        MatchData matchData = new MatchData();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 50, 20, 50)); // Adjust padding as needed

        // Initialize components for Aka and Ao
        ScoreComponent akaScoreComponent = new ScoreComponent(matchData, ParticipantType.AKA);
        ScoreComponent aoScoreComponent = new ScoreComponent(matchData, ParticipantType.AO);
        TimerComponent timerComponent = new TimerComponent(matchData);
        PenaltyComponent penaltyComponent = new PenaltyComponent(matchData);

        // Place components in the pane
        // Layout for placing the components
        HBox mainLayout = new HBox(20);
        mainLayout.getChildren().addAll(
                aoScoreComponent.getComponent(),
                timerComponent.getComponent(),
                akaScoreComponent.getComponent(),
                penaltyComponent.getComponent()
        );
        root.setCenter(mainLayout);
        matchDataView.showMatchDataView(matchData);
        promoKumiteView.showPromoKumiteView(matchData);
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
