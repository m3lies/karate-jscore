package ch.sku.karatescore;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.components.PenaltyComponent;
import ch.sku.karatescore.components.ScoreComponent;
import ch.sku.karatescore.components.TimerComponent;
import ch.sku.karatescore.model.MatchData;
import ch.sku.karatescore.view.FourFifteenView;
import ch.sku.karatescore.view.WKFView;
import ch.sku.karatescore.view.PromoKumiteView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;


public class KarateScoreboardApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Karate Scoreboard");

        WKFView WKFView =new WKFView();
        PromoKumiteView promoKumiteView = new PromoKumiteView();
        FourFifteenView fourFifteenView = new FourFifteenView();
        MatchData matchData = new MatchData();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 50, 20, 50)); // Adjust padding as needed

        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(15));

        // Initialize components for Aka and Ao
        ScoreComponent akaScoreComponent = new ScoreComponent(matchData, ParticipantType.AKA);
        ScoreComponent aoScoreComponent = new ScoreComponent(matchData, ParticipantType.AO);
        TimerComponent timerComponent = new TimerComponent(matchData);
        PenaltyComponent akaPenaltyComponent = new PenaltyComponent(matchData, ParticipantType.AKA);
        PenaltyComponent aoPenaltyComponent = new PenaltyComponent(matchData, ParticipantType.AO);

        Button wkfButton = new Button("WKF");
        wkfButton.setOnAction(e -> WKFView.showMatchDataView(matchData) );
        Button promoKumiteButton = new Button("Promo Kumite");
        promoKumiteButton.setOnAction(e -> promoKumiteView.showPromoKumiteView(matchData) );
        Button fourFifteenButton = new Button("FourFifteen");
        fourFifteenButton.setOnAction(e -> fourFifteenView.showFourFifteenView(matchData) );
        buttonPanel.getChildren().addAll(wkfButton, promoKumiteButton, fourFifteenButton);
        root.setTop(buttonPanel);

        // Place components in the pane
        // Layout for placing the components
        HBox mainLayout = new HBox(20);
        mainLayout.getChildren().addAll(
                aoScoreComponent.getComponent(),
                timerComponent.getComponent(),
                akaScoreComponent.getComponent(),
                akaPenaltyComponent.getComponent(),
                aoPenaltyComponent.getComponent()
        );
        root.setCenter(mainLayout);
        mainLayout.getChildren().add(wkfButton);
        mainLayout.getChildren().add(promoKumiteButton);
        mainLayout.getChildren().add(fourFifteenButton);
        Scene scene = new Scene(root, 1900, 1000);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
