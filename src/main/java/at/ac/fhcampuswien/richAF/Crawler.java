package at.ac.fhcampuswien.richAF;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.*;

@Getter
@Setter
public class Crawler {

    private static final Logger log = LogManager.getLogger(Crawler.class);

    private List<Page> history;
    private Page currentPage;
    private int maxDepth = 5;
    private int currentDepth;

    public Crawler(String uri) throws IOException {
        this.history = new LinkedList<>();
        this.currentPage = new Page(uri);
        this.currentDepth = 0;
    }

    public Crawler(URI uri) throws IOException {
        this.history = new LinkedList<>();
        this.currentPage = new Page(uri);
        this.currentDepth = 0;
    }

    public Crawler(String uri, int maxDepth) throws IOException {
        this.history = new LinkedList<>();
        this.currentPage = new Page(uri);
        this.maxDepth = maxDepth;
        this.currentDepth = 0;
    }

    public Crawler(URI uri, int maxDepth) throws IOException {
        this.history = new LinkedList<>();
        this.currentPage = new Page(uri);
        this.maxDepth = maxDepth;
        this.currentDepth = 0;
    }

    private void cont() throws IOException {
        if (this.maxDepth == 1) return;
        for (URI uri : this.currentPage.getLinks()) {
            Crawler c = new Crawler(uri, this.maxDepth - 1);
        }
    }
}
