package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Class containing the Ollama Service and its API Call
 * @author Stefan
 */
public class OllamaService {
    private HttpClient client;
    private URI baseUri;
    private Config _config;
    private EventManager _em;
    private String model;
    private double temperature;
    private int respondtime;
    /**
     * the first part prompt which will be sent which the API call, the prompt can be set with the Setter
     * the first part contains the question to the LLM like "how many words are in the following text:"
     * the second part, the text which shall get analyzed will get to the prompt with the call of the method
     * @code askOllama
     */
    private String prompt;

    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature() {
        try{
            double t= Double.parseDouble(_config.getProperty("ollama.temperature"));
            this.temperature = t;
            return;
        } catch (NumberFormatException e) {
            _em.logErrorMessage(e);
        }
        this.temperature = 0.8;

    }

    public void SetBaseUri(){
        baseUri = URI.create(_config.getProperty("ollama.endpoint"));

    }



    /**
     * Constructor:
     * sets the local config file which will be needed in the api call
     * creates a httpclient
     * sets the endpoint url of the ollama api
     * @param config
     * @param em EventManager object for logging
     */
    public OllamaService(Config config, EventManager em) {
        _config = config;
        _em = em;
        try {
            respondtime = Integer.parseInt(_config.getProperty("ollama.respondtime"));
        } catch (NumberFormatException e) {
            respondtime = 15;
            _em.logErrorMessage(e);
        }

        client = HttpClient.newHttpClient();
        model = config.getProperty("ollama.model");
        SetBaseUri();
    }

    /**
     * loads the prompt form the configuration file
     */
    public void SetBasePrompt(){
        prompt = _config.getProperty("ollama.baseprompt");
    }


    /**
     *  Creates a json object containing which will be sent to Ollama Api via Post
     *  the response as string of it is returned
     * @param paragraph ... the text the Ollama Model shall analyze
     * @return ... WorkInProgress CompletableFuture is maybe not necesary anymore
     */
    public CompletableFuture<String> askOllama(String paragraph) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", model);
        jsonObject.put("system", this.prompt);
        jsonObject.put("prompt", "Please analyze this article:"  + paragraph);
        //jsonObject.put("prompt", this.prompt+paragraph);
        jsonObject.put("stream", false);
        jsonObject.put("format", "json");
        jsonObject.put("temperature", temperature);


        // request is made asynchron because of the Ollama Client is very unreliable with its responsetimes
        // this can be done with a CompletableFuture can be compare in C# witch awaitable MethodCalls
        return CompletableFuture.supplyAsync(() -> {
            // building the request
            HttpRequest request = HttpRequest.newBuilder()
                    // adding generate to URI for the API Call
                    .uri(baseUri.resolve("/api/generate"))
                    //.header("Accept", "application/json")
                    //although we send it a JSON Object the best practise of users in the web was to use x-www-from-urlencoded as content type
                    // x-www-form-urlencoded is the type where the parameters als like pairs often used in post formular calls in URLs
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString( jsonObject.toString()))
                    .build();
            try {
                // sending it to the Ollama endpoint
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String test= response.body();
                return response.body();
            } catch (Exception e) {
                _em.logErrorMessage(e);
                //_em.logErrorMessage("askOllama:"+ e.getMessage());
            }
            //_em.logWarningMessage("Ollama API response did not return in time");
            return "ERROR WITH OLLAMA";
        });
    }

    /**
     * makes a http get request on the ollama api entry page to verify if the service is running
     * @return CompletableFuture<String> response of the api as CompletableFuture to make an async call of it
     */
    public CompletableFuture<String> isOllamaRunning() {


        return CompletableFuture.supplyAsync(() -> {
            // building the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(baseUri)
                    .timeout(Duration.ofSeconds(respondtime))
                    .GET()
                    .build();
            try {
                // sending it to the Ollama endpoint
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (Exception e) {
                _em.logErrorMessage(e);
                //_em.logErrorMessage("isOllamaRunning:"+ e.getMessage());
            }
            //_em.logWarningMessage("Ollama did not respond in time");
            return "ollama not responding";
        });
    }
}
