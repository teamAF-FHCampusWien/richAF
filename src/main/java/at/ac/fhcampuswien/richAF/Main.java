package at.ac.fhcampuswien.richAF;

import at.ac.fhcampuswien.richAF.crawler.Crawler;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.net.URISyntaxException;

@Log4j2
public class Main {
    public static void main(String[] args) {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
        try {
            Crawler crawler = new Crawler("https://fh-campuswien.ac.at");
            crawler.crawl();
            log.info("Crawl complete.");
            log.info("Created mesh with {} nodes", crawler.getMesh().getNodes().size());
        } catch (IOException | URISyntaxException e) {
           log.error(e);
        }
    }
}