package ch.sku.karatescore.components;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.MatchData;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PenaltyComponent {
    private final VBox component = new VBox();

    public PenaltyComponent(MatchData matchData , ParticipantType participant) {
        for (PenaltyType penalty : PenaltyType.values()) {
            Button penaltyButton = new Button("Toggle " + penalty.name() + " for " + participant.name());
            penaltyButton.setOnAction(e -> matchData.togglePenalty(participant, penalty));

            Label penaltyStatusLabel = new Label();
            penaltyStatusLabel.textProperty().bind(Bindings.when(matchData.penaltyProperty(participant, penalty)).then("Given").otherwise("Not Given"));

            component.getChildren().addAll(penaltyButton, penaltyStatusLabel);
        }
        component.setSpacing(10);
    }
    public VBox getComponent() {
        return component;
    }
}
