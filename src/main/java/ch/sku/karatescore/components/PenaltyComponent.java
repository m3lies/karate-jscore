package ch.sku.karatescore.components;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.model.MatchData;
import ch.sku.karatescore.model.Participant;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class PenaltyComponent {
    @Getter
    private final VBox component = new VBox();

    private final BooleanProperty isChui1Clicked = new SimpleBooleanProperty(false);
    private final BooleanProperty isChui2Clicked = new SimpleBooleanProperty(false);
    private final BooleanProperty isChui3Clicked = new SimpleBooleanProperty(false);
    private final BooleanProperty isHansokuChuiClicked = new SimpleBooleanProperty(false);
    private final BooleanProperty isHansokuClicked = new SimpleBooleanProperty(false);
    private final MatchData matchData;

    public PenaltyComponent(MatchData matchData, Participant participant) {
        this.matchData = matchData;
        for (PenaltyType penalty : PenaltyType.values()) {
            Button penaltyButton = new Button(penalty.name() + participant.getParticipantType().getType());
            penaltyButton.setOnAction(e -> togglePenalty(participant, penalty));

            Label penaltyStatusLabel = new Label();
            switch (penalty) {
                case CHUI1:
                    penaltyStatusLabel.textProperty().bind(Bindings.when(isChui1Clicked).then("Given").otherwise("Not Given"));
                    break;
                case CHUI2:
                    penaltyStatusLabel.textProperty().bind(Bindings.when(isChui2Clicked).then("Given").otherwise("Not Given"));
                    break;
                case CHUI3:
                    penaltyStatusLabel.textProperty().bind(Bindings.when(isChui3Clicked).then("Given").otherwise("Not Given"));
                    break;
                case HANSOKU_CHUI:
                    penaltyStatusLabel.textProperty().bind(Bindings.when(isHansokuChuiClicked).then("Given").otherwise("Not Given"));
                    break;
                case HANSOKU:
                    penaltyStatusLabel.textProperty().bind(Bindings.when(isHansokuClicked).then("Given").otherwise("Not Given"));
                    break;
                default:
                    penaltyStatusLabel.textProperty().bind(Bindings.when(matchData.penaltyProperty(participant.getParticipantType(), penalty)).then("Given").otherwise("Not Given"));
                    break;
            }

            component.getChildren().addAll(penaltyButton, penaltyStatusLabel);
        }
        component.setSpacing(10);
    }

    private void togglePenalty(Participant participant, PenaltyType penalty) {
        switch (penalty) {
            case CHUI1:
                isChui1Clicked.set(!isChui1Clicked.get());
                break;
            case CHUI2:
                isChui2Clicked.set(!isChui2Clicked.get());
                if (isChui2Clicked.get()) {
                    isChui1Clicked.set(true);
                }
                break;
            case CHUI3:
                isChui3Clicked.set(!isChui3Clicked.get());
                if (isChui3Clicked.get()) {
                    isChui1Clicked.set(true);
                    isChui2Clicked.set(true);
                }
                break;
            case HANSOKU_CHUI:
                isHansokuChuiClicked.set(!isHansokuChuiClicked.get());
                if (isHansokuChuiClicked.get()) {
                    isChui1Clicked.set(true);
                    isChui2Clicked.set(true);
                    isChui3Clicked.set(true);
                }
                break;
            case HANSOKU:
                isHansokuClicked.set(!isHansokuClicked.get());
                if (isHansokuClicked.get()) {
                    isChui1Clicked.set(true);
                    isChui2Clicked.set(true);
                    isChui3Clicked.set(true);
                    isHansokuChuiClicked.set(true);
                }
                break;
            default:
                matchData.togglePenalty(participant, penalty);
                break;
        }
    }

}
