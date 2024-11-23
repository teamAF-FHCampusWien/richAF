package at.ac.fhcampuswien.richAF;

import at.ac.fhcampuswien.richAF.data.EventManager;

public class Main {
    public static void main(String[] args) {
        EventManager eventManager = new EventManager("log.txt");
        eventManager.logMessage("Hello World!",Main.class.getName());
    }
}