package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.model.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

public class OllamaService {
    HttpClient client;
    URI baseUri;
    Config _config;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }



    private String prompt;

    public OllamaService(Config config) {
        _config = config;
        client = HttpClient.newHttpClient();
        baseUri = URI.create(_config.getProperty("ollama.endpoint"));
    }

    public void SetBasePrompt(String companyname){
        prompt = "count the positive and negative news about the company "+companyname+" in the following text and return as result only just the count in exact this Format {positve=#;negative=#} instead of the # then the corresponding count number no text of the news in the response: ";

    }



    public CompletableFuture<String> askOllama(String paragraph) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "llama3.2");
        jsonObject.put("prompt", this.prompt+paragraph);
        jsonObject.put("stream", false);


        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(baseUri.resolve("generate"))
                    //.header("Accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString( jsonObject.toString()))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (Exception e) {
                System.out.println(e);
            }

            return "ERROR WITH OLLAMA";
        });
    }
}
