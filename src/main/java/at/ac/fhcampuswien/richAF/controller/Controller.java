package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.ArticleResult;
import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.Config;
import at.ac.fhcampuswien.richAF.model.dao.tblResult;
import at.ac.fhcampuswien.richAF.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;

public class Controller {
    OllamaService _olService;
    Config _config;
    DBService _dbService;
    ServiceScheduler _scheduler;
    ScheduledExecutorService _schedulerExec;
    EventManager _em;
    private int clickCounter = 0;
    int resultcounter=0;
    ScheduledExecutorService resChecker;
    private List<ArticleResult> articles;
    private Map mapFilter;

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
    private Label lblJob;

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

    @FXML
    private ImageView emptyResults;

    @FXML
    private Label nrFeeds;

    @FXML
    private Label nrArticles;

    @FXML
    private Label tickerWin;

    @FXML
    private Label tickerDown;


    // Constructors
    public Controller() {
        String logPath = Paths.get(System.getProperty("java.io.tmpdir"), "richAF.log").toString();
        System.out.println(logPath);
        _em = new EventManager(logPath);
        _config = new Config();
        _olService = new OllamaService(_config, _em);
        _dbService = new DBService(_config, _em);
    }

    // Methods
    @FXML
    public void initialize() {

        OllamaServiceController osc = new OllamaServiceController(lblOllama, cirOllama, ttOllama, _olService, _em);
        _scheduler = new ServiceScheduler(_schedulerExec, pgiJob, _olService, _dbService, _em,_config);

// wird jetzt beim refresh gestartet
//        tgbJobService.setOnAction(event -> {
//            if (tgbJobService.isSelected()) {
//                tgbJobService.setText("Stop Scheduler");
//                _scheduler.startScheduler();
//            } else {
//                tgbJobService.setText("Start Scheduler");
//                _scheduler.stopScheduler();
//                JobService.Abort();
//            }
//        });
        tgbJobService.setStyle("-fx-background-color: #7F8795;");
        tgbJobService.setOnAction(event -> {
            if (tgbJobService.isSelected()) {
                tgbJobService.setStyle("-fx-background-color: green;");
                tgbJobService.setGraphic(new Rectangle(8, 8, Color.GREEN));
            } else {
                tgbJobService.setStyle("-fx-background-color: red;");
                tgbJobService.setGraphic(new Rectangle(8, 8, Color.RED));
                JobService.Abort();
            }
        });

        filterButton.setOnAction(event -> {
            if (filterButton.isSelected()) {
                filterButton.setText(" Close");
                showFilterMenu();

            } else {
                filterButton.setText("");
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), filtermenu);
                slideOut.setToX(-1200);
                slideOut.setInterpolator(Interpolator.EASE_OUT);
                slideOut.play();
                //greyOverlay.toBack();
            }
        });

        welcomeLabel.setOnMouseClicked(event -> showHiddenDevMenu(event));
        lblJob.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openTempDirectory();
            }
        });
    }

    /**
     * öffnet den Explorer zum LogVerzeichnis
     *
     */
    private void openTempDirectory() {
        String tempDir = System.getProperty("java.io.tmpdir");
        File file = new File(tempDir);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * wird vom Refresh Button getriggert
     * startet den Work Prozess des ServiceSchedulers und startet den "Result Checker" der die visualierung von neuen Results triggert
     */
    public void displayResults() {
        _dbService.clearResults();
        resultcounter = _dbService.GetResults().size();
        resChecker = Executors.newScheduledThreadPool(1);


        _scheduler.doWork();
        //checkForNewResults();
        resChecker.scheduleAtFixedRate(this::checkForNewResults, 0, 5, TimeUnit.SECONDS);
        refreshResults();
    }

    /**
     * die Methodes des Result Checkers, wenn neue Results gefunden oder der Prozess beendet ist wird eine Methode getriggert
     * die dann die refreshMEthode im JavaFX Thread invoken kann
     */
    public void checkForNewResults(){
        if ((!_scheduler.isRunning())) {//||(_scheduler==null)
            resChecker.shutdownNow();
            refreshResultsOnFxThread();
            return;
        }

        int count = _dbService.GetResults().size();
        if (resultcounter != count){
            resultcounter = count;
            refreshResultsOnFxThread();

        }

    }

    /**
     * zum invoken der refreshResults methode aus dem scheduler thread
     */
    private void refreshResultsOnFxThread() {
        Platform.runLater(this::refreshResults);
    }

    public void setMapFilterAndRefresh(Map mapFilter) {
        this.mapFilter = mapFilter;
        refreshResults();
    }

    public List<ArticleResult> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleResult> articles) {
        this.articles = articles;
    }


    /**
     * ließt die Filter werte aus und die Results aus der Datenbank und je nach Filtereinstellung werden dann aus
     * den ResulstJsons articleResults erzeugt welche die Datenbasis für die Cards der Oberfläche sind
     *
     */
    public void refreshResults() {
        String filterTick = "ALLE";
        String filterWL = "ALLE";
        String filterM = "ALLE";

        try{
            filterTick =mapFilter.get("TICKER").toString();}
        catch(Exception e){
            filterTick = "ALLE";
        }
        try{
            filterWL=mapFilter.get("WL").toString();}
        catch(Exception e){
            filterWL = "ALLE";
        }
        try{
            filterM=mapFilter.get("MATCH").toString();}
        catch(Exception e){
            filterM = "ALLE";
        }

        articles = new ArrayList<>();
        for (tblResult tblres : _dbService.GetResults())
         {
            ArticleResult article = new ArticleResult(tblres.getStrResponeJson(), _em);
            articles.add(article);
        }

        emptyResults.setVisible(false);

        // Logic to create new cards dynamically
        setHeader();
        try {
            cardsBox.getChildren().clear();
        } catch (Exception e){
            _em.logErrorMessage(e);
        }
        for (ArticleResult article : articles) {
            try {
                    switch (filterTick) {
                        case "":
                        case "ALLE":
                            break;
                            default:
                                if(!filterTick.equals(article.getStock())) continue;
                    }
                switch (filterWL) {
                    case "":
                    case "ALLE":
                        break;

                    case "WIN":
                        if(!article.getTrend().toUpperCase().equals("UP")) continue;
                        break;
                    case "LOSE":
                        if(!article.getTrend().toUpperCase().equals("DOWN")) continue;
                }
                switch (filterM) {
                    case "":
                    case "ALLE":
                        break;
                    default:
                        if(!article.getRelevant().toUpperCase().equals(filterM)) continue;
                        break;

                }

                FXMLLoader loader = new FXMLLoader((getClass().getResource("/result-card.fxml")));
                loader.load();
                ResultController resultController = loader.getController();

                resultController.setProps(article);
                // Add node to parent
                cardsBox.getChildren().add(loader.getRoot());

            } catch (Exception e) {
                _em.logErrorMessage(e);
            }
        }
    }

    public void setHeader() {
        int nrOfArticles = _dbService.GetResults().size();
        nrFeeds.setText(String.valueOf(nrOfArticles));

        int nrCrawled = _dbService.getPages().size();
        nrArticles.setText(String.valueOf(nrCrawled));

        // =====================================================
        // 1) Build frequency map for UP trends
        // =====================================================
        Map<String, Integer> stockCountUp = new HashMap<>();
        // =====================================================
        // 2) Build frequency map for DOWN trends
        // =====================================================
        Map<String, Integer> stockCountDown = new HashMap<>();

        List<tblResult> allResults = _dbService.GetResults();

        for (tblResult tblres : allResults) {
            // Convert JSON string to ArticleResult
            ArticleResult article = new ArticleResult(tblres.getStrResponeJson(), this._em);

            // If trend is UP, increase the counter for that stock
            if ("UP".equalsIgnoreCase(article.getTrend())) {
                String stock = article.getStock();
                stockCountUp.put(stock, stockCountUp.getOrDefault(stock, 0) + 1);
            }

            // If trend is DOWN, increase the counter for that stock
            if ("DOWN".equalsIgnoreCase(article.getTrend())) {
                String stock = article.getStock();
                stockCountDown.put(stock, stockCountDown.getOrDefault(stock, 0) + 1);
            }
        }

        // =====================================================
        // 3) Find the stock with the highest UP count
        // =====================================================
        Optional<Map.Entry<String, Integer>> maxEntryUp = stockCountUp.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (maxEntryUp.isPresent()) {
            // The stock symbol with the highest UP trend frequency
            String topStockUp = maxEntryUp.get().getKey();
            tickerWin.setText(topStockUp);
        } else {
            tickerWin.setText("N/A");
        }

        // =====================================================
        // 4) Find the stock with the highest DOWN count
        // =====================================================
        Optional<Map.Entry<String, Integer>> maxEntryDown = stockCountDown.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (maxEntryDown.isPresent()) {
            // The stock symbol with the highest DOWN trend frequency
            String topStockDown = maxEntryDown.get().getKey();
            tickerDown.setText(topStockDown);
        } else {
            tickerDown.setText("N/A");
        }

    }

    public void showAddDataSheet() {
        try {
            // Load the bottom sheet from its FXML
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/add-bottomsheet.fxml")));
            loader.setControllerFactory(param -> new AddBottomSheetController(_dbService));
            bottomSheet = loader.load();
            System.out.println("Loaded");
            AddBottomSheetController addBottomSheetController = loader.getController();

            // EventHandler to catch pressing button in AddBottomSheetController
            addBottomSheetController.setOnCancel(event -> {
                hideGreyOverlay();
            });

            addBottomSheetController.setOnSubmit(event -> {
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
            _em.logErrorMessage(e);
        }
    }

    public void showEditDataSheet() {
        try {
            // Load the bottom sheet from its FXML
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/edit-bottomsheet.fxml")));
            loader.setControllerFactory(param -> new EditBottomSheetController(_dbService));
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
            slideUp.setToY(170);
            slideUp.setInterpolator(Interpolator.EASE_OUT);
            slideUp.play();

        } catch (IOException e) {
            _em.logErrorMessage(e);
        }
    }

    public void hideGreyOverlay() {
        greyOverlay.toBack();
    }

    public void showFilterMenu() {
        try {
            FXMLLoader loader = new FXMLLoader((getClass().getResource("/filter-menu.fxml")));
            loader.setControllerFactory(param -> new FilterController(this));
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
            _em.logErrorMessage(e);
        }
    }

    private void showHiddenDevMenu(MouseEvent event) {
        clickCounter++;
        System.out.println("Welcome Label clicked " + clickCounter + " times.");

        if (clickCounter == 5) {
            clickCounter = 0;

            try {
                FXMLLoader loader = new FXMLLoader((getClass().getResource("/dev-menu.fxml")));
                loader.setControllerFactory(param -> new DevMenuController(_config,_olService));
                devMenuRoot = loader.load();
                DevMenuController devMenuController = loader.getController();

                rootStackPane.getChildren().add(devMenuRoot);
                devMenuController.loadDataFromConfig();

            } catch (IOException e) {
                _em.logErrorMessage(e);
            }
        }
    }

}


