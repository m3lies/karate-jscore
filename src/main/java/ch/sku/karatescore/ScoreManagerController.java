package ch.sku.karatescore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ScoreManagerController {

    @FXML
    private Label participant1ScoreLabel;

    @FXML
    private Label participant2ScoreLabel;

    private int[] scores = {0, 0, 0}; // Initial scores for two participants: Yuko, Waza-Ari, Ippon

    // Point values for Yuko, Waza-Ari, and Ippon
    private final int[] pointValues = {1, 2, 3};

    @FXML
    private void initialize() {
        updateScoreLabels();
    }

    @FXML
    private void addScoreForParticipant1(int scoreIndex) {
        scores[scoreIndex]++;
        updateScoreLabels();
    }

    @FXML
    private void addScoreForParticipant2(int scoreIndex) {
        scores[scoreIndex + 3]++;
        updateScoreLabels();
    }

    @FXML
    private void removeScoreForParticipant1(int scoreIndex) {
        if (scores[scoreIndex] > 0) {
            scores[scoreIndex]--;
            updateScoreLabels();
        }
    }

    @FXML
    private void removeScoreForParticipant2(int scoreIndex) {
        if (scores[scoreIndex + 3] > 0) {
            scores[scoreIndex + 3]--;
            updateScoreLabels();
        }
    }

    @FXML
    private void resetScores() {
        scores = new int[]{0, 0, 0, 0, 0, 0}; // Reset scores for both participants
        updateScoreLabels();
    }

    private void updateScoreLabels() {
        participant1ScoreLabel.setText(getScoreString(0, 1, 2));
        participant2ScoreLabel.setText(getScoreString(3, 4, 5));
    }

    private String getScoreString(int yukoIndex, int wazaAriIndex, int ipponIndex) {
        return "Participant Total Score:\n" +
                "Yuko: " + scores[yukoIndex] + " (" + pointValues[0] + " points)\n" +
                "Waza-Ari: " + scores[wazaAriIndex] + " (" + pointValues[1] + " points)\n" +
                "Ippon: " + scores[ipponIndex] + " (" + pointValues[2] + " points)";
    }
}
