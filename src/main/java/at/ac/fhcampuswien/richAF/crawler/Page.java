package at.ac.fhcampuswien.richAF.crawler;

import at.ac.fhcampuswien.richAF.data.EventManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@Setter
public class Page implements Comparable<Page> {

    private URI uri;
    private URI domain;
    private String rawContent;
    private Integer statusCode;
    private Set<URI> links;
    private Set<URI> disallowedLinks;
    private EventManager _em;

    public Page(URI uri, EventManager em) {
        this.uri = uri;
        Pair<Integer, String> result = getContent(this.uri);
        this.rawContent = result.getRight();
        this.statusCode = result.getLeft();
        this._em = em;
        this.links = findLinks();
    }

    private void parseRobots(URI uri) {
        String robotsContent = getContent(uri).getRight();

    }

    private String readInputStream(InputStream inputStream) {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder content = new StringBuilder();
        try {
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            this._em.logErrorMessage("Error reading input stream: " + e.getMessage());
        }
        return content.toString();
    }

    private Pair<Integer, String> getContent(URI uri) {
        try {
            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            String content = readInputStream(con.getInputStream());
            con.disconnect();
            return Pair.of(status, content);
        } catch (IOException e) {
            this._em.logErrorMessage("Error getting content: " + e.getMessage());
            return Pair.of(404, "");
        }
    }

    private String parseLink(String link) {
        if (!link.startsWith("http") && !link.startsWith("/") && (link.contains(".") && !link.endsWith(".html"))) {
            this._em.logWarningMessage("Link not supported: " + link);
            throw new IllegalArgumentException();
        }
        if (link.startsWith("http")) return link;
        if (link.startsWith("//")) return "https:" + link;
        return this.uri.getScheme() + "://" + this.uri.getAuthority() + link;
    }

    private Set<URI> findLinks() {
        Set<String> rawLinks = new HashSet<>();
        Pattern htmlLinkReg = Pattern.compile("href=['\"](?<url>[^#\"]+?)['\"]");
        Matcher matcher = htmlLinkReg.matcher(this.rawContent);
        while (matcher.find()) {
            String rawResult = matcher.group("url");
            String parsedLink;
            try {
                parsedLink = this.parseLink(rawResult);
            } catch (IllegalArgumentException e) {
                if (rawResult.endsWith(".css")){
                    _em.logErrorMessage(e);
                    break;
                }
                continue;
            }
            rawLinks.add(parsedLink);
        }
        Set<URI> parsedLinks = new HashSet<>();
        for (String link : rawLinks) {
            try {
                parsedLinks.add(URI.create(link));
            } catch (IllegalArgumentException e) {
                this._em.logErrorMessage("Error creating URI: " + link);
            }
        }
        return parsedLinks;
    }

    @Override
    public int compareTo(Page o) {
        return this.uri.toString().compareTo(o.uri.toString());
    }
}
