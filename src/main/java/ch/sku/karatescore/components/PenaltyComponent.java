package ch.sku.karatescore.components;

import ch.sku.karatescore.model.MatchData;
import javafx.scene.layout.VBox;

public class PenaltyComponent {
    private final VBox component = new VBox();

    public PenaltyComponent(MatchData matchData) {}
    public VBox getComponent() {
        return component;
    }
}
