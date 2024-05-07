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
    private final VBox component = new VBox(10);  // Spacing between elements

    public PenaltyComponent(Participant participant) {
        this.component.getStyleClass().add("penalties-container");
        for (PenaltyType penalty : PenaltyType.values()) {
            Button penaltyButton = new Button(penalty.name());
            penaltyButton.getStyleClass().add("penalty-button");

            // Binding the button style class to the penalty property
            penaltyButton.styleProperty().bind(Bindings
                    .when(participant.getPenaltyProperty(penalty))
                    .then("-fx-background-color: darkgray; -fx-text-fill: white;")
                    .otherwise("-fx-background-color: white; -fx-text-fill: black;"));

            penaltyButton.setOnAction(e -> togglePenalty(participant, penalty));

            // Optional: Displaying penalty status (if you want to show/hide text dynamically)
            Label penaltyStatusLabel = new Label(penalty.name());
            penaltyStatusLabel.visibleProperty().bind(participant.getPenaltyProperty(penalty));
            penaltyStatusLabel.getStyleClass().add("penalty-status");

            this.component.getChildren().addAll(penaltyButton, penaltyStatusLabel);
        }
    }

    private void togglePenalty(Participant participant, PenaltyType penalty) {
        BooleanProperty currentPenaltyProperty = participant.getPenaltyProperty(penalty);
        boolean currentStatus = currentPenaltyProperty.get();
        currentPenaltyProperty.set(!currentStatus);

        if (currentPenaltyProperty.get()) {
            // Setting all lower-level penalties based on the hierarchy
            if (penalty == PenaltyType.HANSOKU) {
                participant.getPenaltyProperty(PenaltyType.HANSOKU_CHUI).set(true);
            }
            if (penalty.ordinal() >= PenaltyType.HANSOKU_CHUI.ordinal()) {
                participant.getPenaltyProperty(PenaltyType.CHUI3).set(true);
            }
            if (penalty.ordinal() >= PenaltyType.CHUI3.ordinal()) {
                participant.getPenaltyProperty(PenaltyType.CHUI2).set(true);
            }
            if (penalty.ordinal() >= PenaltyType.CHUI2.ordinal()) {
                participant.getPenaltyProperty(PenaltyType.CHUI1).set(true);
            }
        }
    }

}
