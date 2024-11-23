package at.ac.fhcampuswien.richAF.crawler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Getter
@Setter
public class Page implements Comparable<Page> {

    private URI uri;
    private URI domain;
    private String rawContent;
    private Integer statusCode;
    private Set<URI> links;

    public Page(String uri) throws IOException {
        this.uri = URI.create(uri);
        this.rawContent = getContent().getRight();
        this.statusCode = getContent().getLeft();
        this.links = findLinks();
    }

    public Page(URI uri) throws IOException {
        this.uri = uri;
        this.rawContent = getContent().getRight();
        this.statusCode = getContent().getLeft();
        this.links = findLinks();
    }

    private Pair<Integer, String> getContent() {
        try {
            HttpURLConnection con = (HttpURLConnection) this.uri.toURL().openConnection();
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
        } catch (IOException e) {
            log.error(e);
            return Pair.of(404, "");
        }
    }

    private String parseLink(String link) throws URISyntaxException {
        log.info("Parsing link {}", link);
        if (link.startsWith("http") | link.startsWith("https")) {
            return link;
        }
        if (link.startsWith("/")) {
            if (link.startsWith("//")) {
                return "https:" + link;
            }
            return this.uri.getScheme() + "://" + this.uri.getAuthority() + link;
        }
        throw new URISyntaxException(link, "Invalid link");
    }

    private Set<URI> findLinks() {
        Set<String> rawLinks = new HashSet<>();
        Pattern htmlLinkReg = Pattern.compile("href=['\"](?<url>[^#]+?)['\"]");
        Matcher matcher = htmlLinkReg.matcher(this.rawContent);
        while (matcher.find()) {
            String rawResult = this.rawContent.substring(matcher.start() + 6, matcher.end() - 1);
            String parsedLink;
            try {
                parsedLink = this.parseLink(rawResult);
            } catch (URISyntaxException e) {
                log.error(e);
                continue;
            }
            rawLinks.add(parsedLink);
        }
        rawLinks.forEach(l -> log.info("{}", l)); // TODO: Remove unnecessary logging
        return rawLinks.stream().map(URI::create).collect(Collectors.toSet());
    }

    @Override
    public int compareTo(Page o) {
        return this.uri.toString().compareTo(o.uri.toString());
    }
}
