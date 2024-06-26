package ch.sku.karatescore.services;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.ScoreType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class ScoreService {
    private final ObservableMap<ParticipantType, ObservableMap<ScoreType, IntegerProperty>> scores = FXCollections.observableHashMap();
    private final ObservableMap<ParticipantType, IntegerProperty> totalScores = FXCollections.observableHashMap();

    public ScoreService() {
        for (ParticipantType type : ParticipantType.values()) {
            ObservableMap<ScoreType, IntegerProperty> scoreMap = FXCollections.observableHashMap();
            for (ScoreType scoreType : ScoreType.values()) {
                scoreMap.put(scoreType, new SimpleIntegerProperty(0));
            }
            scores.put(type, scoreMap);
            totalScores.put(type, new SimpleIntegerProperty(0));
        }
    }

    public IntegerProperty getScoreProperty(ParticipantType type, ScoreType scoreType) {
        return scores.get(type).get(scoreType);
    }

    public IntegerProperty getTotalScoreProperty(ParticipantType type) {
        return totalScores.get(type);
    }

    public void addScore(ParticipantType type, ScoreType scoreType) {
        IntegerProperty scoreProp = getScoreProperty(type, scoreType);
        scoreProp.set(scoreProp.get() + 1);
        updateTotalScore(type);
    }

    public void subtractScore(ParticipantType type, ScoreType scoreType) {
        IntegerProperty scoreProp = getScoreProperty(type, scoreType);
        if (scoreProp.get() > 0) {
            scoreProp.set(scoreProp.get() - 1);
            updateTotalScore(type);
        }
    }

    private void updateTotalScore(ParticipantType type) {
        int totalScore = 0;
        totalScore += getScoreProperty(type, ScoreType.YUKO).get();
        totalScore += 2 * getScoreProperty(type, ScoreType.WAZARI).get();
        totalScore += 3 * getScoreProperty(type, ScoreType.IPPON).get();
        totalScores.get(type).set(totalScore);
    }

    public void resetScores() {
        for (ParticipantType type : ParticipantType.values()) {
            for (ScoreType scoreType : ScoreType.values()) {
                getScoreProperty(type, scoreType).set(0);
            }
            updateTotalScore(type);
        }
    }
}
