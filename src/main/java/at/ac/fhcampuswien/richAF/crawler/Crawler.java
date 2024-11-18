package at.ac.fhcampuswien.richAF.crawler;

import at.ac.fhcampuswien.richAF.mesh.Mesh;
import at.ac.fhcampuswien.richAF.mesh.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
@Getter
@Setter
public class Crawler {

    private Mesh mesh = new Mesh();

    private Node currentNode;
    private int maxDepth = 2;
    private int currentDepth = 0;

    public Crawler() {
    }

    public Crawler(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void crawl(String uri) throws IOException {
        this.currentNode = mesh.createNode(new Page(uri), null);
    }

    // Steps of the crawler
    // 1: Generate page and node and parse links for a given URI
    // 2: Check if mesh already contains some of the page links (those can be omitted)
    // 3: Add node of generated page to mesh
    // TODO:    4: Crawl all remaining page links and start from the top
    /*
    public void crawl() {
        this.currentNode = mesh.createNode(new Page(uri), null);
    }
     */
}
