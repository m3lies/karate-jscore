package ch.sku.karatescore.components;

import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class PenaltyComponent {
    private final VBox component = new VBox(10);  // Main container with spacing
    private final PenaltyService penaltyService;

    public PenaltyComponent(Participant participant, PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
        HBox labelContainer = new HBox(10);
        HBox buttonContainer = new HBox(10);

        for (PenaltyType penalty : PenaltyType.values()) {
            Label penaltyStatusLabel = new Label(penalty.name());
            penaltyStatusLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participant.getParticipantType(), penalty));
            penaltyStatusLabel.managedProperty().set(true);

            Button penaltyButton = new Button(penalty.name());
            penaltyButton.styleProperty().bind(
                    Bindings.when(penaltyService.getPenaltyProperty(participant.getParticipantType(), penalty))
                            .then("-fx-background-color: darkgray; -fx-text-fill: white;")
                            .otherwise("-fx-background-color: white; -fx-text-fill: black;")
            );
            penaltyButton.setOnAction(e -> penaltyService.togglePenalty(participant.getParticipantType(), penalty));

            labelContainer.getChildren().add(penaltyStatusLabel);
            buttonContainer.getChildren().add(penaltyButton);
        }

        this.component.getChildren().addAll(labelContainer, buttonContainer);
    }


    private void togglePenalty(Participant participant, PenaltyType penalty) {
        BooleanProperty currentPenaltyProperty =penaltyService.getPenaltyProperty(participant.getParticipantType(), penalty);
        boolean isCurrentlyActive = currentPenaltyProperty.get();

        // If activating a penalty that is currently inactive:
        if (!isCurrentlyActive) {
            // First deactivate all penalties
            deactivateHigherPenalties(participant);

            // Then activate this penalty and all lesser penalties
            activateLowerPenalties(participant, penalty);
        } else {
            // If the penalty is already active and clicked again, only deactivate higher penalties
            deactivateHigherPenaltiesExcludingCurrent(participant, penalty);
        }
    }

    private void deactivateHigherPenalties(Participant participant) {
        for (PenaltyType pt : PenaltyType.values()) {
            penaltyService.getPenaltyProperty(participant.getParticipantType(), pt).set(false);
        }
    }

    private void activateLowerPenalties(Participant participant, PenaltyType penalty) {
        for (PenaltyType pt : PenaltyType.values()) {
            if (pt.ordinal() <= penalty.ordinal()) {
                penaltyService.getPenaltyProperty(participant.getParticipantType(), penalty).set(true);
            }
        }
    }

    private void deactivateHigherPenaltiesExcludingCurrent(Participant participant, PenaltyType penalty) {
        for (PenaltyType pt : PenaltyType.values()) {
            if (pt.ordinal() > penalty.ordinal()) {
                penaltyService.getPenaltyProperty(participant.getParticipantType(), pt).set(false);
            }
        }
    }

}
