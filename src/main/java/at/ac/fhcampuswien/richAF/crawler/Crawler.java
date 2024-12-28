package at.ac.fhcampuswien.richAF.crawler;

import at.ac.fhcampuswien.richAF.mesh.Mesh;
import at.ac.fhcampuswien.richAF.mesh.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.net.URI;

@Log4j2
@Getter
@Setter
public class Crawler {

    private Mesh mesh = new Mesh();

    private Node currentNode;
    private int maxDepth = 2;
    private int currentDepth = 0;

    public Crawler(String uri) {
        this.currentNode = this.mesh.createNode(new Page(URI.create(uri)), this.currentNode);
    }

    public Crawler(String uri, int maxDepth) {
        this.currentNode = this.mesh.createNode(new Page(URI.create(uri)), this.currentNode);
        this.maxDepth = maxDepth;
    }

    // Steps of the crawler
    // 1: Generate page and node and parse links for a given URI
    // 2: Check if mesh already contains some of the page links (those can be omitted)
    // 3: Add node of generated page to mesh
    // TODO:    4: Crawl all remaining page links and start from the top
    public void crawl() {
        currentDepth++;
        if (this.currentDepth == this.maxDepth) return;
        for (URI link : this.currentNode.getPage().getLinks()) {
            mesh.createNode(new Page(link), this.currentNode);
            log.info("New node created for URI: {}", link.toString());
        }
    }
}
