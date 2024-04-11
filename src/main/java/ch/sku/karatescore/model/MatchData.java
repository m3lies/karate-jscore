package ch.sku.karatescore.model;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.EnumMap;
import java.util.Map;

public class MatchData {
    private final Map<ParticipantType, IntegerProperty> yukoScores = new EnumMap<>(ParticipantType.class);
    private final Map<ParticipantType, IntegerProperty> wazaAriScores = new EnumMap<>(ParticipantType.class);
    private final Map<ParticipantType, IntegerProperty> ipponScores = new EnumMap<>(ParticipantType.class);
    private final Map<ParticipantType, ReadOnlyIntegerWrapper> totalScores = new EnumMap<>(ParticipantType.class);

    private final ObservableMap<ParticipantType, ObservableMap<PenaltyType, BooleanProperty>> penalties = FXCollections.observableHashMap();
    // Penalties for AKA

    private final StringProperty timer = new SimpleStringProperty("00:00");

    public MatchData() {
        for (ParticipantType participant : ParticipantType.values()) {
            yukoScores.put(participant, new SimpleIntegerProperty(0));
            wazaAriScores.put(participant, new SimpleIntegerProperty(0));
            ipponScores.put(participant, new SimpleIntegerProperty(0));
            totalScores.put(participant, new ReadOnlyIntegerWrapper());
            ObservableMap<PenaltyType, BooleanProperty> participantPenalties = FXCollections.observableHashMap();
            for (PenaltyType penalty : PenaltyType.values()) {
                participantPenalties.put(penalty, new SimpleBooleanProperty(false));
            }
            penalties.put(participant, participantPenalties);
        }
    }
    public BooleanProperty penaltyProperty(ParticipantType participant, PenaltyType penalty) {
        return penalties.get(participant).get(penalty);
    }

    public void togglePenalty(ParticipantType participant, PenaltyType penalty) {
        switch (penalty) {
            case CHUI3:
                // When toggling Chui 3, ensure Chui 1 and Chui 2 are set to true
                penaltyProperty(participant, PenaltyType.CHUI1).set(true);
                penaltyProperty(participant, PenaltyType.CHUI2).set(true);
                penaltyProperty(participant, PenaltyType.CHUI3).set(true);
                break;
            case CHUI2:
                // Ensure Chui 1 is set when Chui 2 is toggled
                penaltyProperty(participant, PenaltyType.CHUI1).set(true);
                penaltyProperty(participant, PenaltyType.CHUI2).set(true);
                break;
            case CHUI1:
                penaltyProperty(participant, PenaltyType.CHUI1).set(true);
                break;
            // Handle other penalties without specific cascading logic
            default:
                BooleanProperty penaltyProp = penaltyProperty(participant, penalty);
                penaltyProp.set(!penaltyProp.get());
                break;
        }
    }

    // Getters and setters for properties
    public IntegerProperty yukoProperty(ParticipantType type) {
        return yukoScores.get(type);
    }

    public IntegerProperty wazaAriProperty(ParticipantType type) {
        return wazaAriScores.get(type);
    }

    public IntegerProperty ipponProperty(ParticipantType type) {
        return ipponScores.get(type);
    }

    public ReadOnlyIntegerProperty totalScoreProperty(ParticipantType type) {
        return totalScores.get(type).getReadOnlyProperty();
    }

    public void addYuko(ParticipantType type) {
        yukoScores.get(type).set(yukoScores.get(type).get() + 1);
        updateTotalScore(type);
    }

    public void addWazaAri(ParticipantType type) {
        wazaAriScores.get(type).set(wazaAriScores.get(type).get() + 2);
        updateTotalScore(type);
    }

    public void addIppon(ParticipantType type) {
        ipponScores.get(type).set(ipponScores.get(type).get() + 3);
        updateTotalScore(type);
    }

    private void updateTotalScore(ParticipantType type) {
        int total = yukoScores.get(type).get() +
                wazaAriScores.get(type).get() * 2 +
                ipponScores.get(type).get() * 3;
        totalScores.get(type).set(total);
    }

    public StringProperty timerProperty() {
        return timer;
    }


}
