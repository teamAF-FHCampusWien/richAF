package at.ac.fhcampuswien.richAF.mesh;

import at.ac.fhcampuswien.richAF.crawler.Page;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Getter
@Setter
public class Mesh {

    private List<Node> nodes = new ArrayList<>();

    public Node createNode(Page page, Node parent) {
        if (this.nodes.stream().map(n -> n.getPage().getUri().toString()).collect(Collectors.toSet()).contains(page.getUri().toString())) {
            return this.nodes.stream().filter(n -> n.getPage().getUri().toString().equals(page.getUri().toString())).findFirst().orElse(null);
        }
        Node node = new Node(UUID.randomUUID(), page, parent, new ArrayList<>());
        nodes.add(node);
        return node;
    }
}
