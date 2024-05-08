package ch.sku.karatescore.components;

import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.Participant;
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

    public PenaltyComponent(Participant participant) {
        HBox labelContainer = new HBox(10);  // Container for labels
        HBox buttonContainer = new HBox(10);  // Container for buttons

        for (PenaltyType penalty : PenaltyType.values()) {
            Label penaltyStatusLabel = new Label(penalty.name());
            penaltyStatusLabel.visibleProperty().bind(participant.getPenaltyProperty(penalty));  // Visibility bound to the penalty's BooleanProperty
            penaltyStatusLabel.managedProperty().set(true);  // Always managed, to maintain spacing

            Button penaltyButton = new Button(penalty.name());
            penaltyButton.styleProperty().bind(
                    Bindings.when(participant.getPenaltyProperty(penalty))
                            .then("-fx-background-color: darkgray; -fx-text-fill: white;")
                            .otherwise("-fx-background-color: white; -fx-text-fill: black;")
            );
            penaltyButton.setOnAction(e -> togglePenalty(participant, penalty));

            labelContainer.getChildren().add(penaltyStatusLabel);  // Add each label to the label container
            buttonContainer.getChildren().add(penaltyButton);  // Add each button to the button container
        }

        this.component.getChildren().addAll(labelContainer, buttonContainer);  // Add both containers to the main component
    }

    private void togglePenalty(Participant participant, PenaltyType penalty) {
        BooleanProperty currentPenaltyProperty = participant.getPenaltyProperty(penalty);
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
            participant.getPenaltyProperty(pt).set(false);
        }
    }

    private void activateLowerPenalties(Participant participant, PenaltyType penalty) {
        for (PenaltyType pt : PenaltyType.values()) {
            if (pt.ordinal() <= penalty.ordinal()) {
                participant.getPenaltyProperty(pt).set(true);
            }
        }
    }

    private void deactivateHigherPenaltiesExcludingCurrent(Participant participant, PenaltyType penalty) {
        for (PenaltyType pt : PenaltyType.values()) {
            if (pt.ordinal() > penalty.ordinal()) {
                participant.getPenaltyProperty(pt).set(false);
            }
        }
    }

}
