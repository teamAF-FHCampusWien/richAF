package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.controller.Controller;
import at.ac.fhcampuswien.richAF.crawler.Crawler;
import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.*;
import at.ac.fhcampuswien.richAF.model.dao.tblJob;
import at.ac.fhcampuswien.richAF.model.dao.tblPage;
import at.ac.fhcampuswien.richAF.model.dao.tblSource;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
/**
 * Class containing the Methods for JobCreation for the Ollama Service and JobExcecution with the OllamaService
 * @author Stefan
 */
public class JobService {

    public static void Abort(){
        wecanrun = false;
    };
    public static boolean wecanrun;

    /**
     * Creates the Jobs for the OllamaService from the Saved Webpages in the Database
     * Parsing the webpage will be done with  <a href="https://jsoup.org/">Jsoup</a>
     *
     * @param dbservice ... dbservice Object
     * @param pcounter ... number of Paragraphs of a webpage which shall be used for one Ollama Request
     * @param em EventManager object for logging
     */
    public static void CreateJobs(DBService dbservice, int pcounter, EventManager em) {
        JobService.wecanrun=true;

        // getting the Pages from DB which have not been processed
        ArrayList<tblPage> lisPages = dbservice.getPages(Enums_.Status.NEW);
        // less than 1 Paragraph is not allowed
        if (pcounter < 1){
            em.logErrorMessage("CreateJobs: pcounter to low");
            return;
        }
        for (tblPage p : lisPages) {
            if (!wecanrun) {
                em.logInfoMessage("CreateJobs: manual abort");
                return;
            }
            // get the page String into a structured Document Object with Jsoup
            Document doc = Jsoup.parse(p.getStrPage());
            // page enters status processing
            dbservice.UpdateStatus(p, Enums_.Status.PROCESSING);
            try {
                String doctype = doc.documentType().name();
                //currently we only want to parse html pages
                if (doctype.equals("html")) {

                    String title = doc.getElementsByTag("title").text();
                    if (title != null && !title.equals("")) {
                        p.setStrTitle(title);
                        dbservice.updatePage(p);
                    }
                    //getting only the <p> from the html-document
                    Elements paragraphs= doc.select("p");
                    String strParagraph="";
                    for (int i = 0; i < paragraphs.size(); i++) {
                        strParagraph = strParagraph + paragraphs.get(i).text();
                        if ((i+1) % pcounter == 0) {
                            // when the number of paragraphs is reached a Job will be created
                            dbservice.addJob(strParagraph, p.getId());
                            strParagraph = "";

                        }
                    }
                    if (strParagraph != "")
                        dbservice.addJob(strParagraph ,p.getId());
                    // page done
                    dbservice.UpdateStatus(p, Enums_.Status.PROCESSED);
                    em.logInfoMessage(String.format("Page with id %s processed", p.getId()));
                }
                // page is not a html page, the page is not relevant anymore
                else {
                    dbservice.UpdateStatus(p, Enums_.Status.PROCESSED_FAILURE);
                    em.logInfoMessage(String.format("Page with id %s cant be processed", p.getId()));
                }


            } catch (Exception e) {
                dbservice.UpdateStatus(p, Enums_.Status.PROCESSED_FAILURE);
                em.logErrorMessage(e);
                em.logErrorMessage(String.format("CreateJobs processing with doctype[%s] failed:%s",doc.documentType(),e.getMessage()));
            }
        }



    }

    /**
     * Processes and executes the Jobs
     * the OllamaService get the Prompts and text send to it and answers with a response
     * the response should contain a counter of positive and negative news and this will be saved in the DB as a tblResult
     * @param ollamaService ...  the OllamaService
     * @param dbservice ... the DB service
     * @param em EventManager object for logging
     */
    public static void ExecuteJobs(OllamaService ollamaService, DBService dbservice, EventManager em) {
        JobService.wecanrun=true;
        // getting the jobs from DB which have not been processed
        ArrayList<tblJob> lisJobs = dbservice.getJobs(Enums_.Status.NEW);
        ArrayList<tblPage> lisPages = dbservice.getPages(Enums_.Status.ALL);
        ollamaService.SetBaseUri();
        ollamaService.SetBasePrompt();
        ollamaService.setTemperature();
        for (tblJob j : lisJobs) {
            if (!wecanrun) {
                em.logWarningMessage("ExecuteJobs: manual abort");
                return;
            }
            // for the response of the ollamaservice
            String response = "";
            // job enters status processing
            dbservice.UpdateStatus(j, Enums_.Status.PROCESSING);
            em.logInfoMessage(String.format("ExecuteJobs processing Job-id %s",j.getId()));

            try {
                // sending the Request to Ollama
                response = ollamaService.askOllama(j.getStrParagraphs()).get();
                if(!response.contains("response")){
                    em.logWarningMessage(String.format("ExecuteJobs processing failed: Ollama did not responded correctly (Job-id %s)",j.getId()));
                    dbservice.UpdateStatus(j, Enums_.Status.PROCESSED_FAILURE);
                    continue;
                }
                // the response is in a json format so it will be casted in a json object
                JSONObject jsonResponseObject = new JSONObject(response);
                // the section response holds the created answer/result from the service
                String strResponse = trimJsonString(jsonResponseObject.getString("response"));
                if (strResponse != null){
                    if (strResponse.matches( "^\\{.*\\}$")){
                        JSONObject jsonResult= new JSONObject(strResponse);
                        if ((jsonResult.has("stock")) && (jsonResult.length()<5))
                            if (jsonResult.get("stock") != ""){ // TO DO splitten
                                Map<String, Object> orderedMap = new LinkedHashMap<>();

                                orderedMap.put("stock", jsonResult.get("stock"));



                                Optional<tblPage> result = lisPages.stream()
                                        .filter(page -> page.getId() == j.getIntPageID())
                                        .findFirst();
                                tblPage page = null;
                                if (result.isPresent())
                                    page = result.get();

                                orderedMap.put("title", page != null ? page.getStrTitle() : "");

                                if (jsonResult.has("summary"))
                                    orderedMap.put("summary", jsonResult.get("summary"));
                                else
                                    orderedMap.put("summary","");

                                if (jsonResult.has("relevant"))
                                    orderedMap.put("relevant", jsonResult.get("relevant"));
                                else
                                    orderedMap.put("relevant","NO");

                                if (jsonResult.has("trend"))
                                    orderedMap.put("trend", jsonResult.get("trend"));
                                else
                                    orderedMap.put("trend","NONE");

                                orderedMap.put("source", page != null ? page.getStrLink() : "");

                                JSONObject jsonFinalResult= new JSONObject(orderedMap);
                                //adding the result to the DB
                                dbservice.addResult(j.getId(), jsonFinalResult.toString());
                            }

                    }
                }


            } catch (InterruptedException | ExecutionException e) {
                em.logErrorMessage(e);
            }
            dbservice.UpdateStatus(j, Enums_.Status.PROCESSED);
        }
    }

    /**
     *
     * @param jsonString
     * @return
     * @author Copilot
     */
    private static String trimJsonString(String jsonString) {
        // Entferne Whitespaces vor dem ersten '{'
        jsonString = jsonString.replaceAll("^[^\\{]*\\{", "{");
        // Entferne Whitespaces nach dem letzten '}'
        jsonString = jsonString.replaceAll("\\}[^\\}]*$", "}");
        return jsonString;
    }


    /**
     * starten den Crawldurchgang, für jede Source in tblsource wird ein crawler erzeugt und der vorgang durchgeführt
     * die depth wird aus der config bezogen
     * @param dbservice
     * @param conf
     * @param em
     */
    public static void CrawlerCrawl(DBService dbservice, Config conf, EventManager em) {
        JobService.wecanrun = true;
        if (conf.getProperty("crawleroff").equals("true")) {
            em.logWarningMessage("Crawler deactivated");
            return;
        }
        ArrayList<tblSource> lissources = dbservice.getSources();
        for (tblSource s : lissources) {
            if (!wecanrun) {
                em.logInfoMessage("CreateJobs: manual abort");
                return;
            }
            int depth=2;
            try{
                depth=  Integer.parseInt(conf.getProperty("crawlerdepth"));
            } catch (NumberFormatException e) {
                em.logErrorMessage(e);
            }
            Crawler crawler = new Crawler(s.getStrUrl(),depth, em);
            crawler.crawl();

            dbservice.SavePagesFromCrawler(crawler);
        }
    }
}
