package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.ArticleResult;
import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.Config;
import at.ac.fhcampuswien.richAF.model.dao.tblResult;
import at.ac.fhcampuswien.richAF.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import javafx.scene.input.MouseEvent;


public class Controller {
    OllamaService _olService;
    Config _config;
    DBService _dbService;
    ServiceScheduler _scheduler;
    ScheduledExecutorService _schedulerExec;
    EventManager _em;
    private int clickCounter = 0;

    // Elements
    @FXML
    private Label lblOllama;

    @FXML
    private Circle cirOllama;

    @FXML
    private Tooltip ttOllama;

    @FXML
    private ToggleButton tgbJobService;

    @FXML
    private ProgressIndicator pgiJob;

    @FXML
    private ScrollPane resultCardContainer;

    @FXML
    private HBox cardsBox;

    @FXML
    private Button refreshButton;

    @FXML
    private StackPane bottomSheetContainer;

    @FXML
    private VBox bottomSheet;

    @FXML
    private VBox editBottomSheet;

    @FXML
    private Button addData;

    @FXML
    private Button editData;

    @FXML
    private StackPane rootStackPane;

    @FXML
    private Rectangle greyOverlay;

    @FXML
    private VBox filtermenu;

    @FXML
    private ToggleButton filterButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private StackPane devMenuRoot;


    // Constructors
    public Controller() {
        String logPath = Paths.get(System.getProperty("java.io.tmpdir"), "richAF.log").toString();
        _em = new EventManager(logPath);
        _config = new Config();
        _olService = new OllamaService(_config, _em);
        _dbService = new DBService(_config, _em);
    }

    // Methods
    @FXML
    public void initialize() {

        OllamaServiceController osc = new OllamaServiceController(lblOllama, cirOllama, ttOllama, _olService);
        _scheduler = new ServiceScheduler(_schedulerExec, pgiJob, _olService, _dbService, _em);
        _scheduler.setPcounter(Integer.parseInt(_config.getProperty("jobservice.pcounter")));
        // Beispiel für die Interaktion mit dem webcrawler
        // _dbService.SavePagesFromCrawler(new Crawler( _dbService.getSources().getLast().getStrUrl()));
        //for (tblSource ts : _dbService.getSources())
        //    _dbService.SavePagesFromCrawler(new Crawler(ts.getStrUrl()));

        tgbJobService.setOnAction(event -> {
            if (tgbJobService.isSelected()) {
                tgbJobService.setText("Stop Scheduler");
                _scheduler.startScheduler();
            } else {
                tgbJobService.setText("Start Scheduler");
                _scheduler.stopScheduler();
                JobService.Abort();
            }
        });

        filterButton.setOnAction(event -> {
            if (filterButton.isSelected()) {
                filterButton.setText("Done");
                showFilterMenu();

            } else {
                filterButton.setText("Filter");
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), filtermenu);
                slideOut.setToX(-1200);
                slideOut.setInterpolator(Interpolator.EASE_OUT);
                slideOut.play();
                //greyOverlay.toBack();
            }
        });

        welcomeLabel.setOnMouseClicked(event -> showHiddenDevMenu(event));

    }

    public void displayResults() {
        //TODO: connect real results, for now only dummy data is shown
        List<ArticleResult> articles = new ArrayList<>();
        for (tblResult tblres : _dbService.GetResults())
         {
            ArticleResult article = new ArticleResult(tblres.getStrResponeJson());
            articles.add(article);
        }

        // Logic to create new cards dynamically
        for (ArticleResult article : articles) {
            try {
                FXMLLoader loader = new FXMLLoader((getClass().getResource("/result-card.fxml")));
                loader.load();
                ResultController resultController = loader.getController();

                // Set title
                resultController.setCardTitle(article.getStock());
                // Set summary
                resultController.setCardSummary(article.getSummary());
                // Add node to parent
                cardsBox.getChildren().add(loader.getRoot());

            } catch (IOException e) {
                //TODO: Add to logs @Botan(?)
                e.printStackTrace();
            }
        }

    }

    public void showAddDataSheet() {
        try {
            // Load the bottom sheet from its FXML
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/add-bottomsheet.fxml")));
            bottomSheet = loader.load();
            System.out.println("Loaded");
            AddBottomSheetController addBottomSheetController = loader.getController();

            // EventHandler to catch pressing button in AddBottomSheetController
            addBottomSheetController.setOnCancel(event -> {
                hideGreyOverlay();
            });

            bottomSheet.setPrefWidth(bottomSheetContainer.getWidth());

            // Add the bottom sheet to the container
            greyOverlay.toFront();
            rootStackPane.getChildren().add(bottomSheet);

            // Set the initial position off-screen (below the current view)
            bottomSheet.setTranslateY(bottomSheetContainer.getHeight());

            // Animate it sliding into view
            TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), bottomSheet);
            slideUp.setToY(165);
            slideUp.setInterpolator(Interpolator.EASE_OUT);
            slideUp.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showEditDataSheet() {
        try {
            // Load the bottom sheet from its FXML
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/edit-bottomsheet.fxml")));
            editBottomSheet = loader.load();
            EditBottomSheetController editBottomSheetController = loader.getController();

            // Event Listener
            editBottomSheetController.setOnCancel(event -> {
                hideGreyOverlay();
            });

            editBottomSheet.setPrefWidth(bottomSheetContainer.getWidth());

            // Add the bottom sheet to the container
            greyOverlay.toFront();
            rootStackPane.getChildren().add(editBottomSheet);

            // Set the initial position off-screen (below the current view)
            editBottomSheet.setTranslateY(bottomSheetContainer.getHeight());

            // Animate it sliding into view
            TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), editBottomSheet);
            slideUp.setToY(165);
            slideUp.setInterpolator(Interpolator.EASE_OUT);
            slideUp.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hideGreyOverlay() {
        greyOverlay.toBack();
    }

    public void showFilterMenu() {
        try {
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/filter-menu.fxml")));
            filtermenu = loader.load();
            FilterController filterController = loader.getController();

            filterController.setOnDone(event -> {
                hideGreyOverlay();
            });

            //greyOverlay.toFront();
            rootStackPane.getChildren().add(filtermenu);
            filtermenu.setTranslateX(-rootStackPane.getWidth());
            filtermenu.setTranslateY(25);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), filtermenu);
            slideIn.setToX(-790);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            slideIn.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showHiddenDevMenu(MouseEvent event) {
        clickCounter++;
        System.out.println("Welcome Label clicked " + clickCounter + " times.");

        if (clickCounter == 5) {
            clickCounter = 0;

            try {
                FXMLLoader loader = new FXMLLoader((getClass().getResource("/dev-menu.fxml")));
                devMenuRoot = loader.load();
                DevMenuController devMenuController = loader.getController();

                rootStackPane.getChildren().add(devMenuRoot);
                devMenuController.loadDataFromConfig();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


