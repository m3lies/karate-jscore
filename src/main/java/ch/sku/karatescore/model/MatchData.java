package ch.sku.karatescore.model;

import ch.sku.karatescore.commons.ParticipantType;
import javafx.beans.property.*;

import java.util.EnumMap;
import java.util.Map;

public class MatchData {
    private final Map<ParticipantType, IntegerProperty> yukoScores = new EnumMap<>(ParticipantType.class);
    private final Map<ParticipantType, IntegerProperty> wazaAriScores = new EnumMap<>(ParticipantType.class);
    private final Map<ParticipantType, IntegerProperty> ipponScores = new EnumMap<>(ParticipantType.class);
    private final Map<ParticipantType, ReadOnlyIntegerWrapper> totalScores = new EnumMap<>(ParticipantType.class);

    private final IntegerProperty penalties = new SimpleIntegerProperty(0);
    private final StringProperty timer = new SimpleStringProperty("00:00");

    public MatchData() {
        for (ParticipantType type : ParticipantType.values()) {
            yukoScores.put(type, new SimpleIntegerProperty(0));
            wazaAriScores.put(type, new SimpleIntegerProperty(0));
            ipponScores.put(type, new SimpleIntegerProperty(0));
            totalScores.put(type, new ReadOnlyIntegerWrapper());
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

    public IntegerProperty penaltiesProperty() {
        return penalties;
    }

    public StringProperty timerProperty() {
        return timer;
    }


}
