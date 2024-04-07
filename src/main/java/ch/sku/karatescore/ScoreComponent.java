package ch.sku.karatescore;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ScoreComponent {
    private final VBox component = new VBox();
    private int yukoScore = 0;
    private int wazaAriScore = 0;
    private int ipponScore = 0;
    private final Label scoreLabel;
    private final Label totalScoreLabel;

    public ScoreComponent(String name, String alignment, String color) {
        Label nameLabel = new Label(name);
        scoreLabel = new Label("Yuko: 0, Waza-ari: 0, Ippon: 0");
        totalScoreLabel = new Label("Total: 0");

        Button yukoButton = new Button("Yuko");
        yukoButton.setOnAction(e -> updateScore("yuko"));
        Button wazaAriButton = new Button("Waza-ari");
        wazaAriButton.setOnAction(e -> updateScore("waza-ari"));
        Button ipponButton = new Button("Ippon");
        ipponButton.setOnAction(e -> updateScore("ippon"));

        component.getChildren().addAll(nameLabel, totalScoreLabel, scoreLabel, yukoButton, wazaAriButton, ipponButton);
        component.setAlignment(Pos.CENTER);
        component.setSpacing(10);

        // Set background color
        BackgroundFill backgroundFill = new BackgroundFill(Color.web(color), CornerRadii.EMPTY, Insets.EMPTY);
        component.setBackground(new Background(backgroundFill));

        if ("right".equals(alignment)) {
            component.setAlignment(Pos.CENTER_RIGHT);
        } else if ("left".equals(alignment)) {
            component.setAlignment(Pos.CENTER_LEFT);
        }
    }

    private void updateScore(String scoreType) {
        switch (scoreType) {
            case "yuko":
                yukoScore += 1;
                break;
            case "waza-ari":
                wazaAriScore += 2;
                break;
            case "ippon":
                ipponScore += 3;
                break;
        }
        int totalScore = yukoScore + wazaAriScore + ipponScore; // Calculate the total score
        scoreLabel.setText(String.format("Yuko: %d, Waza-ari: %d, Ippon: %d", yukoScore, wazaAriScore, ipponScore));
        totalScoreLabel.setText("Total: " + totalScore); // Update the total score display
    }

    public VBox getComponent() {
        return component;
    }
}
