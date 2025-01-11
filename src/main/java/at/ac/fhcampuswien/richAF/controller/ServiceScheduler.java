package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.services.DBService;
import at.ac.fhcampuswien.richAF.services.JobService;
import at.ac.fhcampuswien.richAF.services.OllamaService;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * Class contains the Scheduler to run the JobService Methods
 * it has its own progressIndicator icon to show if its running so its part a control and part a scheduler
 * it's method will be called by a toogleButton from the Controller
 * @author Stefan
 */
public class ServiceScheduler{

    private ScheduledExecutorService scheduler;
    private ProgressIndicator progressIndicator;
    private boolean isRunning;
    private OllamaService _olService;
    private DBService _dbService;
    private EventManager _em;
    private int pcounter;
    private int companyid;
    public int getPcounter() {
        return pcounter;
    }

    public void setPcounter(int pcounter) {
        this.pcounter = pcounter;
    }

    public int getCompanyid() {
        return companyid;
    }

    public void setCompanyid(int companyid) {
        this.companyid = companyid;
    }

    /**
     * Constructor: parameters are taken and put into the local variables. the pcounters value is set to 5 and the company to 0, to force that a companyid has to be chosen
     * @param sch ScheduledExecutorService for the scheduled JobService methods running
     * @param pro ProgressIndicator so signal running
     * @param ols OllamaService
     * @param dbs DBService
     * @param em EventManager for logging
     */
    public ServiceScheduler(ScheduledExecutorService sch, ProgressIndicator pro , OllamaService ols, DBService dbs, EventManager em){
        scheduler = sch;
        progressIndicator = pro;
        _em = em;
        isRunning = false;
        progressIndicator.setVisible(false);
        _olService = ols;
        _dbService = dbs;
        pcounter = 5;
        companyid = 0;
    }

    /**
     * starts the Scheduler with a new Thread, this Thread will call createAndExecuteJobs in 30 seconds
     */
    public void startScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::createAndExecuteJobs, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * Stops the Scheduler service so it will not call in another 30 seconds
     * WARNING: it won't stop the running JobService Method
     */
    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Calls if not running the CreateJobs asynchronous and after this completion it will call ExecuteJobs asynchron, this will prevent the UI from freezing while this Tasks are running
     */
    private void createAndExecuteJobs() {
        if (!isRunning) {
            isRunning = true;
            Platform.runLater(() -> progressIndicator.setVisible(true));
            CompletableFuture.runAsync(() -> JobService.CreateJobs(_dbService,pcounter,companyid,_em))
                    .thenRunAsync(() -> JobService.ExecuteJobs(_olService,_dbService,_em))
                    .thenRun(() -> {
                        isRunning = false;
                        Platform.runLater(() -> progressIndicator.setVisible(false));
                    });
        }
    }
}
