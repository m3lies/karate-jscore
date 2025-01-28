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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class PenaltyComponent {
    @Getter
    private HBox component;
    private final Participant participant;
    private final PenaltyService penaltyService;
    private final boolean includeButtons;

    public PenaltyComponent(Participant participant, PenaltyService penaltyService, boolean includeButtons) {
        this.participant = participant;
        this.penaltyService = penaltyService;
        this.includeButtons = includeButtons;
        initComponent();
    }

    private void initComponent() {
        component = new HBox(10);
        component.setPadding(new Insets(10));
        component.setAlignment(Pos.CENTER);
        ParticipantType participantType = participant.getParticipantType();
        for (PenaltyType penalty : PenaltyType.values()) {
            component.getChildren().add(createVBox(penaltyService, participantType, penalty, includeButtons));
        }
    }

    private VBox createVBox(PenaltyService penaltyService, ParticipantType participantType, PenaltyType penalty, boolean includeButtons) {
        VBox penaltyBox = new VBox(5);
        penaltyBox.setAlignment(Pos.CENTER);

        Label penaltyLabel = new Label(penalty.getName());
        penaltyLabel.getStyleClass().add("penalty-label");

        penaltyLabel.visibleProperty().bind(penaltyService.getPenaltyProperty(participantType, penalty));
        penaltyLabel.managedProperty().set(true);
        penaltyLabel.setAlignment(Pos.CENTER);

        Pane penaltyLabelContainer = new Pane();
        penaltyLabelContainer.getChildren().add(penaltyLabel);

        penaltyBox.getChildren().add(penaltyLabelContainer);

        if (includeButtons) {
            Button penaltyButton = new Button(penalty.getName());
            penaltyButton.setOnAction(e -> penaltyService.togglePenalty(participantType, penalty));
            penaltyBox.getChildren().add(penaltyButton);
        }
        return penaltyBox;
    }

}
