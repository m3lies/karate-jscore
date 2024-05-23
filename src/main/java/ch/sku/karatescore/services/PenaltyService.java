package ch.sku.karatescore.services;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class PenaltyService {
    private final ObservableMap<ParticipantType, ObservableMap<PenaltyType, BooleanProperty>> penalties = FXCollections.observableHashMap();

    public PenaltyService() {
        for (ParticipantType type : ParticipantType.values()) {
            ObservableMap<PenaltyType, BooleanProperty> participantPenalties = FXCollections.observableHashMap();
            for (PenaltyType penalty : PenaltyType.values()) {
                participantPenalties.put(penalty, new SimpleBooleanProperty(false));
            }
            penalties.put(type, participantPenalties);
        }
    }

    public BooleanProperty getPenaltyProperty(ParticipantType participantType, PenaltyType penalty) {
        return penalties.get(participantType).get(penalty);
    }

    private void activateLowerPenalties(ParticipantType type, PenaltyType penalty) {
        for (PenaltyType pt : PenaltyType.values()) {
            if (pt.ordinal() <= penalty.ordinal()) {
                penalties.get(type).get(pt).set(true);
            }
        }
    }

    private void deactivateHigherPenalties(ParticipantType type, PenaltyType penalty) {
        for (PenaltyType pt : PenaltyType.values()) {
            if (pt.ordinal() >= penalty.ordinal()) {
                penalties.get(type).get(pt).set(false);
            }
        }
    }

    public void togglePenalty(ParticipantType type, PenaltyType penalty) {
        BooleanProperty penaltyProperty = getPenaltyProperty(type, penalty);
        boolean isCurrentlyActive = penaltyProperty.get();

        if (!isCurrentlyActive) {
            activateLowerPenalties(type, penalty);
        } else {
            deactivateHigherPenalties(type, penalty);
        }
    }

    public void resetPenalties() {
        for (ParticipantType type : ParticipantType.values()) {
            for (PenaltyType penalty : PenaltyType.values()) {
                penalties.get(type).get(penalty).set(false);
            }
        }
    }
}
