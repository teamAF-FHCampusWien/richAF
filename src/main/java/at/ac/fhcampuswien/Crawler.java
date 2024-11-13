package at.ac.fhcampuswien;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Crawler {

    private static final Logger log = LogManager.getLogger(Crawler.class);

    private final URL url;
    private List<URL> history;
    private List<URL> links;


    public Crawler(URL url) {
        this.url = url;

        history = new LinkedList<>();
        history.add(this.url);

        links = new ArrayList<>();

        String pageContent;
        int status;
        try {
            Pair<Integer, String> result = getPageContent();
            pageContent = result.getRight();
            status = result.getLeft();
        } catch (IOException e) {
            log.error(e);
            pageContent = "[ERROR] Could not get page content";
        }
    }

    private Pair<Integer, String> getPageContent() throws IOException {
        HttpURLConnection con = (HttpURLConnection) this.url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        return Pair.of(status, content.toString());
    }

    private void parseHtmlLinks(String input) {
        // TODO: Find all links in the HTML string and parse out their URLs
        String htmlLinkReg = "/.*<a.*/g";

    }
}
