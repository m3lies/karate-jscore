package ch.sku.karatescore.components;

import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.services.PenaltyService;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PenaltyComponent {
    private final Participant participant;
    private final PenaltyService penaltyService;
    private final VBox component;

    public PenaltyComponent(Participant participant, PenaltyService penaltyService, boolean includeButtons) {
        this.participant = participant;
        this.penaltyService = penaltyService;
        this.component = new VBox(10);

        HBox penaltyContainer = new HBox(10);
        for (PenaltyType penaltyType : PenaltyType.values()) {
            BooleanProperty penaltyProperty = penaltyService.getPenaltyProperty(participant.getParticipantType(), penaltyType);
            Button penaltyButton = new Button(penaltyType.name());
            penaltyButton.setStyle("-fx-font-size: 20px;");
            penaltyButton.visibleProperty().bind(penaltyProperty);
            penaltyButton.managedProperty().bind(penaltyButton.visibleProperty());
            penaltyContainer.getChildren().add(penaltyButton);
        }

        component.getChildren().add(penaltyContainer);
        if (includeButtons) {
            Button resetButton = new Button("Reset Penalties");
            resetButton.setOnAction(e -> penaltyService.resetPenalties());
            component.getChildren().add(resetButton);
        }
    }

    public VBox getComponent() {
        return component;
    }
}
