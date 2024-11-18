package at.ac.fhcampuswien.richAF.mesh;

import at.ac.fhcampuswien.richAF.crawler.Page;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.UUID;

@Log4j2
@Getter
@Setter
public class Node implements Comparable<Node> {

    private UUID uuid;
    private Page page;
    private Node parent;
    private List<Node> children;

    public Node(UUID uuid, Page page, Node parent, List<Node> children) {
        this.uuid = uuid;
        this.page = page;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public int compareTo(Node o) {
        return this.page.compareTo(o.getPage());
    }
}
