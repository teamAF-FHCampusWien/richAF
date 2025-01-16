package at.ac.fhcampuswien.richAF.controller;

import at.ac.fhcampuswien.richAF.services.OllamaService;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Class contains a Control consisting of Standardcontrols to singal the connectivity to the ollama service
 * @author Stefan
 */
public class OllamaServiceController {
    public Label lblOllama;
    public Circle cirOllama;
    public Tooltip ttOllama;
    private OllamaService olService;

    private int _status;


    public int get_status() {
        return _status;
    }

    /**
     * when the property is set bei the Set-Method the displayServiceStatus is automatically called
     * @param _status
     */
    public void set_status(int _status) {
        this._status = _status;
        displayServiceStatus();
    }

    /**
     * Constructor: takes the given Controls to use it as his. sets the Status to pending and activates the AnimationTimer which
     * makes the status check every 30 seconds
     * @param lbl the Label-Control
     * @param cir the Circle-DrawingElement
     * @param tt the Tooltip-Control
     * @param ols the OllamaService
     */
    public OllamaServiceController(Label lbl, Circle cir, Tooltip tt, OllamaService ols) {
        lblOllama = lbl;
        cirOllama = cir;
        ttOllama = tt;
        olService = ols;
        _status = -1;
        displayServiceStatus();

        // animationtimer like Backgroundworker but for JavaFx
        // calls checkServiceStatus every 30 seconds
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {

                if (now - lastUpdate >= 30_000_000_000L) {
                    checkServiceStatus();
                    lastUpdate = now;
                }
            }
        };

        timer.start();
    }

    /**
     * sets the circle and the tooltip text according to the Status of the property
     */
    void displayServiceStatus() {
        switch (_status) {
            case 0: {
                ttOllama.setText("not responding");
                cirOllama.setFill(Color.RED);
                break;
            }
            case 1: {
                ttOllama.setText("running");
                cirOllama.setFill(Color.GREEN);
                break;
            }
            default: {
                ttOllama.setText("pending");
                cirOllama.setFill(Color.ORANGE);
                break;
            }
        }
    }

    /**
     * makes asynchronous call to check the Status of the Ollama Api by a Webrequest
     */
    void checkServiceStatus() {
        set_status(-1);
        olService.isOllamaRunning().thenAccept(status -> {
                    if (status.toLowerCase().matches(".*\\b(is|running)\\b.*"))
                        set_status(1);
                    else
                        set_status(0);
                })// if an Exception happens then the service was not available
                .exceptionally(ex -> {
                    set_status(0);
                    return null;
                });

    }
}

