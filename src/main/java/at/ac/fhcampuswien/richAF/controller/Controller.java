package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.crawler.Crawler;
import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.Config;
import at.ac.fhcampuswien.richAF.model.dao.tblSource;
import at.ac.fhcampuswien.richAF.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;

import java.util.concurrent.*;


public class Controller {
    OllamaService _olService;
    Config _config;
    DBService _dbService;
    ServiceScheduler _scheduler;
    ScheduledExecutorService _schedulerExec;
    EventManager _em;

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

    public Controller() {
        // ACHTUNG muss noch plattform und rechner unabhängig gewählt werden
        _em = new EventManager("D:\\temp\\richAF.log");
        _config = new Config();
        _olService = new OllamaService(_config,_em);
        _dbService = new DBService(_config,_em);
    }

    public void initialize() {
        OllamaServiceControl osc= new OllamaServiceControl(lblOllama, cirOllama, ttOllama , _olService);
        _scheduler = new ServiceScheduler(_schedulerExec,pgiJob, _olService, _dbService,_em);
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

    }

}

