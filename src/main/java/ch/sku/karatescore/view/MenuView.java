package ch.sku.karatescore.view;

import ch.sku.karatescore.model.Participant;
import ch.sku.karatescore.services.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

public class MenuView {
    private static final String WKF = "WKF";
    private static final String PROMO_KUMITE = "Promo Kumite";
    private static final String FOUR_FIFTEEN = "4 x 15";
    private static MenuView instance;
    private final Participant aka;
    private final Participant ao;
    private final TimerService timerService;
    private final ScoreService scoreService;
    private final PenaltyService penaltyService;
    private final SenshuService senshuService;
    private final CategoryService categoryService;
    @Getter
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private Stage currentModeStage;
    private Stage editViewStage;

    private MenuView(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService, CategoryService categoryService) {
        this.aka = aka;
        this.ao = ao;
        this.timerService = timerService;
        this.scoreService = scoreService;
        this.penaltyService = penaltyService;
        this.senshuService = senshuService;
        this.categoryService = categoryService;
        this.stage = new Stage();

        initializeUI();
        this.stage.setOnCloseRequest(event -> {
            event.consume(); // Consume the event to prevent the window from closing
            this.stage.setIconified(true); // Minimize the window instead
        });
    }

    public static MenuView getInstance(Participant aka, Participant ao, TimerService timerService, ScoreService scoreService, PenaltyService penaltyService, SenshuService senshuService, CategoryService categoryService) {
        if (instance == null) {
            instance = new MenuView(aka, ao, timerService, scoreService, penaltyService, senshuService, categoryService);
        }
        return instance;
    }

    private void initializeUI() {
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm()); // Correct reference to CSS

        Button btnOpenWKF = new Button(WKF);
        btnOpenWKF.setOnAction(e -> openMode(new WKFView(aka, ao, timerService, scoreService, penaltyService, senshuService, categoryService).getStage(), WKF));

        Button btnOpenPromoKumite = new Button(PROMO_KUMITE);
        btnOpenPromoKumite.setOnAction(e ->
                openMode(new PromoKumiteView(aka, ao, scoreService, penaltyService, categoryService).getStage(), PROMO_KUMITE)
        );

        Button btnOpenFourFifteen = new Button(FOUR_FIFTEEN);
        btnOpenFourFifteen.setOnAction(e -> openMode(new FourFifteenView(aka, ao, timerService, penaltyService, categoryService).getStage(), FOUR_FIFTEEN));

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
        stage.setTitle("Select a mode");
    }

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    private void openMode(Stage specificModeStage, String modeName) {
        if (currentModeStage != null) {
            currentModeStage.close();
        }

        if (editViewStage != null) {
            editViewStage.close();
        }

        // Create and show EditView
        if(modeName == WKF) {
            EditView editView = new EditView(aka, ao, timerService, scoreService, penaltyService, senshuService, categoryService, specificModeStage, modeName);
            editViewStage = editView.getStage();
            editViewStage.show();
        }
        else if( modeName == PROMO_KUMITE){
            EditPromoKumiteView editPromoKumiteView = new EditPromoKumiteView(aka, ao, timerService, scoreService, penaltyService, senshuService, categoryService, specificModeStage, modeName);
            editViewStage = editPromoKumiteView.getStage();
            editViewStage.show();
        }else{
            EditFourFifteenView editFourFifteenView = new EditFourFifteenView(aka, ao, timerService, scoreService, penaltyService, senshuService, categoryService, specificModeStage, modeName);
            editViewStage = editFourFifteenView.getStage();
            editViewStage.show();
        }
        // Set up and show the specific mode stage
        currentModeStage = specificModeStage;

        // Position the specific mode stage on the next available screen if there is more than one screen
        List<Screen> screens = Screen.getScreens();
        if (screens.size() > 1) {
            // Get the second screen
            Screen secondScreen = screens.get(1);

            // Set the stage to the second screen
            currentModeStage.setX(secondScreen.getVisualBounds().getMinX());
            currentModeStage.setY(secondScreen.getVisualBounds().getMinY());
            currentModeStage.setWidth(secondScreen.getVisualBounds().getWidth());
            currentModeStage.setHeight(secondScreen.getVisualBounds().getHeight());
            currentModeStage.setFullScreen(true);
        }

        // Minimize the MenuView
        stage.setIconified(true);

        currentModeStage.setOnHidden(event -> {
            currentModeStage = null;
            stage.setIconified(false); // Restore the MenuView when the specific mode stage is closed
        });
        currentModeStage.show();
    }

    public void closeCurrentMode() {
        if (currentModeStage != null) {
            currentModeStage.close();
            currentModeStage = null;
        }
        if (editViewStage != null) {
            editViewStage.close();
            editViewStage = null;
        }
    }
}
