package ch.sku.karatescore.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TimerComponent {
    private VBox component = new VBox();
    private Label timerLabel;

    public TimerComponent() {
        timerLabel = new Label("00:00");
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button setButton = new Button("Set");

        // Timer set fields and buttons would go here
        // You would also handle the timer logic, starting, stopping, and updating the timerLabel

        component.getChildren().addAll(timerLabel, startButton, stopButton, setButton);
        component.setAlignment(Pos.CENTER);
        component.setSpacing(10);
        // Style the component with CSS or JavaFX setters
    }

    public VBox getComponent() {
        return component;
    }
}

