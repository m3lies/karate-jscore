package ch.sku.karatescore.components;

import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

@Getter
public class PenaltyComponent {
    private final VBox component = new VBox(10);  // Main container with spacing
    private final PenaltyService penaltyService;

    public PenaltyComponent(Participant participant, PenaltyService penaltyService, boolean includeButtons) {
        this.penaltyService = penaltyService;
        HBox labelContainer = new HBox(10);
        HBox buttonContainer = new HBox(10);

        for (PenaltyType penalty : PenaltyType.values()) {
            Label penaltyStatusLabel = new Label(penalty.name());
            penaltyStatusLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participant.getParticipantType(), penalty));
            penaltyStatusLabel.managedProperty().bind(penaltyService.getPenaltyProperty(participant.getParticipantType(), penalty));

            labelContainer.getChildren().add(penaltyStatusLabel);

            if (includeButtons) {
                Button penaltyButton = new Button(penalty.name());
                penaltyButton.styleProperty().bind(
                        Bindings.when(penaltyService.getPenaltyProperty(participant.getParticipantType(), penalty))
                                .then("-fx-background-color: darkgray; -fx-text-fill: white;")
                                .otherwise("-fx-background-color: white; -fx-text-fill: black;")
                );
                penaltyButton.setOnAction(e -> penaltyService.togglePenalty(participant.getParticipantType(), penalty));

                buttonContainer.getChildren().add(penaltyButton);
            }
        }

        this.component.getChildren().add(labelContainer);
        if (includeButtons) {
            this.component.getChildren().add(buttonContainer);
        }
    }
}
