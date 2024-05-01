package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.MatchData;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PromoKumiteView {
    private final VBox component = new VBox();
    private Stage dataStage = null;

    public void showPromoKumiteView(MatchData matchData) {
        if(dataStage == null ){
            Stage dataStage = new Stage();
            dataStage.setTitle("Promo Kumite");

            BorderPane borderPane = new BorderPane();

            // Timer Label
            Label timerLabel = new Label();
            timerLabel.textProperty().bind(matchData.timerProperty());
            VBox dataLayoutAka = createParticipantScoreLayout(matchData, ParticipantType.AKA);
            VBox dataLayoutAo = createParticipantScoreLayout(matchData, ParticipantType.AO);
            borderPane.setLeft(dataLayoutAka);
            borderPane.setRight(dataLayoutAo);

            Scene dataScene = new Scene(borderPane, 200, 300);
            dataStage.setScene(dataScene);
            dataStage.show();
        }

    }


    public VBox createParticipantScoreLayout(MatchData matchData, ParticipantType type) {
        VBox dataLayout = new VBox(10);
        dataLayout.setPadding(new Insets(15));

        // Participant Label
        Label participantLabel = new Label(type.name());

        // Labels for scores and penalties
        Label yukoLabel = new Label();
        yukoLabel.textProperty().bind(matchData.yukoProperty(type).asString().concat(" Yuko"));

        Label wazaAriLabel = new Label();
        wazaAriLabel.textProperty().bind(matchData.wazaAriProperty(type).asString().concat(" Waza-ari"));

        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(matchData.totalScoreProperty(type).asString().concat("Total"));
        addPenaltyLabels(matchData, type, dataLayout);



        // Add all labels to the VBox
        dataLayout.getChildren().addAll(participantLabel, yukoLabel, wazaAriLabel, totalScoreLabel);

        return dataLayout;


    }
    private void addPenaltyLabels(MatchData matchData, ParticipantType participantType, VBox parent) {
        for (PenaltyType penaltyType : PenaltyType.values()) {
            Label penaltyLabel = new Label();
            penaltyLabel.textProperty().bind(Bindings.when(matchData.penaltyProperty(participantType, penaltyType))
                    .then(penaltyType.toString() + ": Given")
                    .otherwise(penaltyType.toString() + ": Not Given"));
            parent.getChildren().add(penaltyLabel);
        }
    }

}
