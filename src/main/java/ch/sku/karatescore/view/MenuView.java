package ch.sku.karatescore.view;

import ch.sku.karatescore.commons.ParticipantType;
import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.PenaltyService;
import ch.sku.karatescore.services.ScoreService;
import ch.sku.karatescore.services.SenshuService;
import ch.sku.karatescore.services.TimerService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.List;
import java.util.Objects;

public class MenuView {
    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;
    private final ScoreService scoreService;
    private final PenaltyService penaltyService;

    private final SenshuService senshuService;
    private final Stage stage;
    private final BorderPane root = new BorderPane();

    public MenuView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService) {
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.senshuService = senshuService;
        this.stage = new Stage();
        initializeUI();
    }

    private void initializeUI() {
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Correct reference to CSS

        Button btnOpenWKF = new Button("WKF");
        btnOpenWKF.setOnAction(e -> openMode(new WKFView(aka, ao, timerService, scoreService, penaltyService,senshuService ).getStage()));

        Button btnOpenPromoKumite = new Button("Promokumite");
        btnOpenPromoKumite.setOnAction(e -> openMode(new PromoKumiteView(aka, ao, scoreService, penaltyService).getStage()));

        Button btnOpenFourFifteen = new Button("4 x 15 ");
        btnOpenFourFifteen.setOnAction(e -> openMode(new FourFifteenView(aka, ao, timerService, penaltyService).getStage()));

        HBox mainLayout = new HBox(10);
        mainLayout.setAlignment(Pos.CENTER);

        // Add buttons to the main layout
        mainLayout.getChildren().addAll(btnOpenWKF, btnOpenPromoKumite, btnOpenFourFifteen);
        root.setCenter(mainLayout);

        // Apply scaling to the root pane
        Scale scale = new Scale(1, 1);
        root.getTransforms().add(scale);

        Scene scene = new Scene(root, 1000, 500);
        stage.setScene(scene);
        stage.setTitle("Choisir un mode");
    }
    //TODO - Categorie en input

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    private void openMode(Stage modeStage) {
        hide();

        // Get all screens
        List<Screen> screens = Screen.getScreens();
        if (screens.size() > 1) {
            // Get the second screen
            Screen secondScreen = screens.get(1);

            // Set the stage to the second screen
            modeStage.setX(secondScreen.getVisualBounds().getMinX());
            modeStage.setY(secondScreen.getVisualBounds().getMinY());
            modeStage.setWidth(secondScreen.getVisualBounds().getWidth());
            modeStage.setHeight(secondScreen.getVisualBounds().getHeight());
            modeStage.setFullScreen(true);
        }

        modeStage.setOnHidden(event -> show());
        modeStage.show();
    }
}
