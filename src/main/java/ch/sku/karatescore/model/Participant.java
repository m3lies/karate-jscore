package ch.sku.karatescore.model;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;


@Data
public class Participant {
    private ParticipantType participantType;
    @Getter
    private Map<ScoreType, Integer> scores; // Points
    @Getter
    private Map<ScoreType, Integer> scoreCounts; // Counts
    private Map<PenaltyType, BooleanProperty> penalties;

    public Participant(ParticipantType participantType) {
        this.participantType = participantType;
        this.scores = new EnumMap<>(ScoreType.class);
        this.scoreCounts = new EnumMap<>(ScoreType.class);
        this.penalties = new EnumMap<>(PenaltyType.class);
        initializePenalties();
        initializeScores();
    }

    private void initializePenalties() {
        for (PenaltyType penaltyType : PenaltyType.values()) {
            penalties.put(penaltyType, new SimpleBooleanProperty(false));
        }
    }
    public BooleanProperty getPenaltyProperty(PenaltyType penaltyType) {
        return penalties.get(penaltyType);
    }

    private void initializeScores() {
        for (ScoreType scoreType : ScoreType.values()) {
            scores.put(scoreType, 0);  // Initializing all scores to 0
            scoreCounts.put(scoreType, 0);  // Initialize counts to 0
        }
    }


    public int calculateTotalScore() {
        int totalScore = 0;
        totalScore += scores.get(ScoreType.YUKO);  // Direct access using enum keys
        totalScore +=  scores.get(ScoreType.WAZARI);  // Direct access using enum keys
        totalScore +=  scores.get(ScoreType.IPPON);  // Direct access using enum keys
        return totalScore;
    }

    public void addScore(ScoreType scoreType) {
        int currentScore = scores.getOrDefault(scoreType, 0);
        int currentCount = scoreCounts.getOrDefault(scoreType, 0);
        scores.put(scoreType, currentScore + getScoreValue(scoreType));
        scoreCounts.put(scoreType, currentCount + 1);
    }

    public void subtractScore(ScoreType scoreType) {
        if (scoreCounts.get(scoreType) > 0) {
            scores.put(scoreType, scores.get(scoreType) - getScoreValue(scoreType));
            scoreCounts.put(scoreType, scoreCounts.get(scoreType) - 1);
        }
    }

    private int getScoreValue(ScoreType scoreType) {
        return switch (scoreType) {
            case YUKO -> 1;
            case WAZARI -> 2;
            case IPPON -> 3;

        };
    }

}
