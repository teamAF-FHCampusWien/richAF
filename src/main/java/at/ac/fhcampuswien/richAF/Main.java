package at.ac.fhcampuswien.richAF;

import at.ac.fhcampuswien.richAF.crawler.Crawler;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class Main {
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        try {
            crawler.crawl("https://fh-campuswien.ac.at");
        } catch (IOException e) {
           log.error(e);
        }
    }
}