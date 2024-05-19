package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.TimerService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.Objects;

public class MenuView {
    private final Participant aka;
    private final Participant ao;

    private final TimerService timerService;
    private final ScoreService scoreService;
    private final PenaltyService penaltyService;
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    public MenuView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService){

        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.stage = new Stage();
        initializeUI();
    }

    private void initializeUI(){
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Correct reference to CSS

        Button btnOpenWKF = new Button("WKF");
        btnOpenWKF.setOnAction(e -> {
            WKFView wkfView = new WKFView(aka, ao, timerService , scoreService, penaltyService);
            wkfView.show();
        });

        Button btnOpenPromoKumite = new Button("Promokumite");
        btnOpenPromoKumite.setOnAction(e -> {
            PromoKumiteView promoKumiteView = new PromoKumiteView(aka, ao, scoreService, penaltyService);
            promoKumiteView.show();
        });

        Button btnOpenFourFifteen = new Button("4 x 15 ");
        btnOpenFourFifteen.setOnAction(e -> {
            FourFifteenView fourFifteenView = new FourFifteenView(aka, ao, timerService, penaltyService);
            fourFifteenView.show();
        });

        StackPane rootPane = new StackPane(btnOpenWKF);
        StackPane secondPane = new StackPane(btnOpenPromoKumite);
        StackPane thirdPane = new StackPane(btnOpenFourFifteen);
        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        // Add to main layout with AO on the left, AKA on the right
        mainLayout.getChildren().addAll(rootPane,secondPane, thirdPane);
        root.setCenter(mainLayout);

        // Apply scaling to the root pane
        Scale scale = new Scale(1, 1);
        root.getTransforms().add(scale);

        Scene scene = new Scene(root, 500, 250);
        stage.setScene(scene);
        stage.setTitle("Choisir un mode");
        stage.show();
    }

    public void show() { stage.show();}
}
