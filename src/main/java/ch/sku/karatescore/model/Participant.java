package ch.sku.karatescore.model;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.commons.PenaltyType;
import ch.sku.karatescore.commons.ScoreType;
import lombok.Data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;


@Data
public class Participant {
    private ParticipantType participantType;
    private Map<ScoreType, Integer> scores;  // Using EnumMap for type safety and efficiency
    private Map<PenaltyType, Boolean> penalties;  // Using EnumMap here too

    public Participant(ParticipantType participantType) {
        this.participantType = participantType;
        this.scores = new EnumMap<>(ScoreType.class);  // Initialize with ScoreType enum
        this.penalties = new EnumMap<>(PenaltyType.class);  // Initialize with PenaltyType enum
        initializePenalties();
        initializeScores();
    }

    private void initializePenalties() {
        for (PenaltyType penaltyType : PenaltyType.values()) {
            penalties.put(penaltyType, false);  // Initializing all penalties to false
        }
    }

    private void initializeScores() {
        for (ScoreType scoreType : ScoreType.values()) {
            scores.put(scoreType, 0);  // Initializing all scores to 0
        }
    }


    public int calculateTotalScore() {
        int totalScore = 0;
        totalScore += scores.get(ScoreType.YUKO);  // Direct access using enum keys
        totalScore += 2 * scores.get(ScoreType.WAZARI);  // Direct access using enum keys
        totalScore += 3 * scores.get(ScoreType.IPPON);  // Direct access using enum keys
        return totalScore;
    }

    public void addScore(ScoreType scoreType) {
        int scoreValue = getScoreValue(scoreType);
        this.scores.put(scoreType, this.scores.get(scoreType) + scoreValue);
    }

    public void subtractScore(ScoreType scoreType) {
        int currentScore = this.scores.get(scoreType);
        int scoreValue = getScoreValue(scoreType);
        if (currentScore >= scoreValue) {
            this.scores.put(scoreType, currentScore - scoreValue);
        }
    }

    private int getScoreValue(ScoreType scoreType) {
        switch (scoreType) {
            case YUKO:
                return 1;
            case WAZARI:
                return 2;
            case IPPON:
                return 3;
            default:
                return 0;
        }
    }
}
