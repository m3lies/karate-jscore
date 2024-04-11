package ch.sku.karatescore.components;

import ch.sku.karatescore.model.MatchData;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PenaltyComponent {
    private final VBox component = new VBox();

    public PenaltyComponent(MatchData matchData) {
        Button chui1Button = new Button("Toggle Chui 1");
        Label chui1StatusLabel = new Label();
        chui1StatusLabel.textProperty().bind(Bindings.when(matchData.chui1GivenProperty()).then("Given").otherwise("Not Given"));

        Button chui2Button = new Button("Toggle Chui 2");
        chui2Button.setOnAction(e -> matchData.toggleChui2Given());
        Label chui2StatusLabel = new Label();
        chui2StatusLabel.textProperty().bind(Bindings.when(matchData.chui2GivenProperty()).then("Given").otherwise("Not Given"));

        Button chui3Button = new Button("Toggle Chui 3");
        chui3Button.setOnAction(e -> matchData.toggleChui3Given());
        Label chui3StatusLabel = new Label();
        chui3StatusLabel.textProperty().bind(Bindings.when(matchData.chui3GivenProperty()).then("Given").otherwise("Not Given"));

        Button hansokuChuicButton = new Button("Hansoku chui");
        chui3Button.setOnAction(e -> matchData.toggleHansokuChuiGiven());
        Label hansokuChuiStatusLabel = new Label();
        chui3StatusLabel.textProperty().bind(Bindings.when(matchData.hansokuChuiGivenProperty()).then("Given").otherwise("Not Given"));
        Button hansokuButton = new Button("Hansoku");
        chui3Button.setOnAction(e -> matchData.toggleHansokuGiven());
        Label hansokuStatusLabel = new Label();
        chui3StatusLabel.textProperty().bind(Bindings.when(matchData.hansokuGivenProperty()).then("Given").otherwise("Not Given"));
        // Add components to the layout
        component.getChildren().addAll(chui1Button, chui1StatusLabel, chui2Button, chui2StatusLabel, chui3Button, chui3StatusLabel, hansokuChuicButton, hansokuChuiStatusLabel, hansokuButton, hansokuStatusLabel);
        // Setup layout as before...
        // Add labels and buttons to the component
        component.setSpacing(10);
        // Apply CSS or other styling as needed
    }
    public VBox getComponent() {
        return component;
    }
}
