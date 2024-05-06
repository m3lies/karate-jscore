package ch.sku.karatescore.components;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.model.MatchData;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ScoreComponent {
    private final VBox component = new VBox();
    private final MatchData matchData;
    private final ParticipantType participantType;

    public ScoreComponent(MatchData matchData, ParticipantType participantType) {
        this.matchData = matchData;
        this.participantType = participantType;

        Label nameLabel = new Label(participantType.name());

        // Bind the score label directly to the match data properties
        Label yukoLabel = new Label();
        yukoLabel.textProperty().bind(matchData.yukoProperty(participantType).asString().concat(" Yuko"));

        Label wazaAriLabel = new Label();
        wazaAriLabel.textProperty().bind(matchData.wazaAriProperty(participantType).asString().concat(" Waza-ari"));

        Label ipponLabel = new Label();
        ipponLabel.textProperty().bind(matchData.ipponProperty(participantType).asString().concat(" Ippon"));

        Label totalScoreLabel = new Label();
        totalScoreLabel.textProperty().bind(matchData.totalScoreProperty(participantType).asString().concat(" Total"));

        Button yukoButton = new Button("Yuko");
        yukoButton.setOnAction(e -> matchData.addYuko(participantType));

        Button wazaAriButton = new Button("Waza-ari");
        wazaAriButton.setOnAction(e -> matchData.addWazaAri(participantType));

        Button ipponButton = new Button("Ippon");
        ipponButton.setOnAction(e -> matchData.addIppon(participantType));

        // Style the buttons
        String btnClass = participantType.equals(ParticipantType.AKA) ? "btn-danger" : "btn-primary";
        yukoButton.getStyleClass().setAll("btn", btnClass);
        wazaAriButton.getStyleClass().setAll("btn", btnClass);
        ipponButton.getStyleClass().setAll("btn", btnClass);

        component.getChildren().addAll(nameLabel, yukoLabel, wazaAriLabel, ipponLabel, totalScoreLabel, yukoButton, wazaAriButton, ipponButton);
        component.setAlignment(Pos.CENTER);
        component.setSpacing(10);
    }

    public VBox getComponent() {
        return component;
    }
}
