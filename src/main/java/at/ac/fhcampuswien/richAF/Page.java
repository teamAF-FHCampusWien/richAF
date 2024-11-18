package at.ac.fhcampuswien.richAF;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter
public class Page {

    private URI uri;
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

    private Pair<Integer, String> getContent() throws IOException {
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
    }

    private Set<URI> findLinks() {
        Set<String> rawLinks = new HashSet<>();
        Pattern htmlLinkReg = Pattern.compile("href=['\"](?<url>.*?)['\"]");
        Matcher matcher = htmlLinkReg.matcher(this.rawContent);
        while (matcher.find()) {
            String result = this.rawContent.substring(matcher.start() + 6, matcher.end() - 1);
            if (result.startsWith("http") | result.startsWith("https") | result.startsWith("/")) {
                if (result.startsWith("/")) {
                    result = this.uri.toString() + result;
                }
                if (result.contains("#")) {
                    result = result.substring(0, result.indexOf("#"));
                }
                rawLinks.add(result.trim());
            }
        }
        rawLinks.forEach(System.out::println);
        return rawLinks.stream().map(URI::create).collect(Collectors.toSet());
    }
}
