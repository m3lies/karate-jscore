package ch.sku.karatescore.components;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PenaltyComponent {
    private final HBox component;

    public PenaltyComponent(Participant participant, PenaltyService penaltyService, boolean includeButtons) {
        component = new HBox(10);
        component.setPadding(new Insets(10));
        component.setAlignment(Pos.CENTER);
        ParticipantType participantType = participant.getParticipantType();

        for (PenaltyType penalty : PenaltyType.values()) {
            VBox penaltyBox = new VBox(5);
            penaltyBox.setAlignment(Pos.CENTER);

            Label penaltyLabel = new Label(penalty.getName());
            penaltyLabel.getStyleClass().add("penalty-label");
            penaltyLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participantType, penalty));
            penaltyLabel.managedProperty().bind(penaltyService.getPenaltyProperty(participantType, penalty));

            //penaltyLabel.setMinWidth(40);  // Set minimum width for labels
            penaltyLabel.setAlignment(Pos.CENTER);  // Center align text

            if (includeButtons) {
                Button penaltyButton = new Button(penalty.getName());
                penaltyButton.setOnAction(e -> penaltyService.togglePenalty(participantType, penalty));
                penaltyBox.getChildren().addAll(penaltyLabel, penaltyButton);
            } else {
                penaltyBox.getChildren().add(penaltyLabel);
            }

            component.getChildren().add(penaltyBox);
        }
    }

    public HBox getComponent() {
        return component;
    }
}
