package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.model.MatchData;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MatchDataView {
    public void showMatchDataView(MatchData matchData) {
        Stage dataStage = new Stage();
        dataStage.setTitle("Match Data");

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
    private VBox createParticipantScoreLayout(MatchData matchData, ParticipantType type) {
        VBox dataLayout = new VBox(10);
        dataLayout.setPadding(new Insets(15));

        // Participant Label
        Label participantLabel = new Label(type.name());

        // Labels for scores and penalties
        Label yukoLabel = new Label();
        yukoLabel.textProperty().bind(matchData.yukoProperty(type).asString().concat(" Yuko"));

        Label wazaAriLabel = new Label();
        wazaAriLabel.textProperty().bind(matchData.wazaAriProperty(type).asString().concat(" Waza-ari"));

        Label ipponLabel = new Label();
        ipponLabel.textProperty().bind(matchData.ipponProperty(type).asString().concat(" Ippon"));

        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(matchData.totalScoreProperty(type).asString().concat("Total"));
        Label penaltyLabel = new Label();
        penaltyLabel.textProperty().bind(matchData.penaltiesProperty().asString().concat(" Penalties"));


        // Add all labels to the VBox
        dataLayout.getChildren().addAll(participantLabel, yukoLabel, wazaAriLabel, ipponLabel, penaltyLabel, totalScoreLabel);

        return dataLayout;

    }

}
