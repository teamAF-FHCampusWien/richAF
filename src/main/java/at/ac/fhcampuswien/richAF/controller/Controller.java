package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.Config;
import at.ac.fhcampuswien.richAF.model.tblCompany;
import at.ac.fhcampuswien.richAF.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;

import java.util.concurrent.ScheduledExecutorService;


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
    private ComboBox cmbCompany;
    @FXML
    private ToggleButton tgbJobService;
    @FXML
    private ProgressIndicator pgiJob;

    public Controller() {
        _em = new EventManager("D:\\temp\\richAF.log");
        _config = new Config();
        _olService = new OllamaService(_config,_em);
        _dbService = new DBService(_config,_em);
    }

    public void initialize() {
        OllamaServiceControl osc= new OllamaServiceControl(lblOllama, cirOllama, ttOllama , _olService);
        _scheduler = new ServiceScheduler(_schedulerExec,pgiJob, _olService, _dbService,_em);
        _scheduler.setPcounter(Integer.parseInt(_config.getProperty("jobservice.pcounter")));
        ObservableList<tblCompany> items = FXCollections.observableArrayList(_dbService.GetAllCompanys());
        cmbCompany.setItems(items);
        cmbCompany.setOnAction(event -> {_scheduler.setCompanyid(((tblCompany)this.cmbCompany.getSelectionModel().getSelectedItem()).getId());});

        tgbJobService.setOnAction(event -> {
            if (tgbJobService.isSelected()) {
                tgbJobService.setText("Stop Scheduler");
                _scheduler.startScheduler();
                cmbCompany.setDisable(true);
            } else {
                tgbJobService.setText("Start Scheduler");
                _scheduler.stopScheduler();
                JobService.Abort();
                cmbCompany.setDisable(false);
            }
        });

    }

}

