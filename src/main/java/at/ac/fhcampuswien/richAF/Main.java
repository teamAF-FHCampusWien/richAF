package at.ac.fhcampuswien.richAF;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            new Crawler("https://fh-campuswien.ac.at", 1);
        } catch (IOException e) {
            log.error(e);
        }
    }
}