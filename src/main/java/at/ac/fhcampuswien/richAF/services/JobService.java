package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.*;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.ArrayList;
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
     * @param companyid ... id of the company from tblCompany which the Ollama shall use in Prompt and the Job shall create a result for
     * @param em EventManager object for logging
     */
    public static void CreateJobs(DBService dbservice, int pcounter, int companyid, EventManager em) {
        JobService.wecanrun=true;
        if (companyid <= 0) {
            //em.logErrorMessage("CreateJobs: invalid companyid given");
            return;
        }
        // getting the Pages from DB which have not been processed
        ArrayList<tblPage> lisPages = dbservice.getPages(Enums_.Status.NEW, "");
        // less than 1 Paragraph is not allowed
        if (pcounter < 1){
            //em.logErrorMessage("CreateJobs: pcounter to low");
            return;
        }
        for (tblPage p : lisPages) {
            if (!wecanrun) {
                //em.logInfoMessage("CreateJobs: manual abort");
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
                    //getting only the <p> from the html-document
                    Elements paragraphs= doc.select("p");
                    String strParagraph="";
                    for (int i = 0; i < paragraphs.size(); i++) {
                        strParagraph = strParagraph + paragraphs.get(i).text();
                        if ((i+1) % pcounter == 0) {
                            // when the number of paragraphs is reached a Job will be created
                            dbservice.addJob(strParagraph,companyid);
                            strParagraph = "";

                        }
                    }
                    if (strParagraph != "")
                        dbservice.addJob(strParagraph,companyid);
                    // page done
                    dbservice.UpdateStatus(p, Enums_.Status.PROCESSED);
                    //em.logInfoMessage(String.format("Page with id %s processed", p.getId()));
                }
                // page is not a html page, the page is not relevant anymore
                else {
                    dbservice.UpdateStatus(p, Enums_.Status.PROCESSED_FAILURE);
                    //em.logInfoMessage(String.format("Page with id %s cant be processed", p.getId()));
                }


            } catch (Exception e) {
                dbservice.UpdateStatus(p, Enums_.Status.PROCESSED_FAILURE);
                em.logErrorMessage(e);
                //em.logErrorMessage(String.format("CreateJobs processing with doctype[%s] failed:%s",doc.documentType(),e.getMessage()));
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
        //company object which the results will be created for
        tblCompany comp=new tblCompany();

        for (tblJob j : lisJobs) {
            if (!wecanrun) {
                //em.logInfoMessage("ExecuteJobs: manual abort");
                return;
            }
            // for the response of the ollamaservice
            String response = "";
            // job enters status processing
            dbservice.UpdateStatus(j, Enums_.Status.PROCESSING);
            //get the Company for this Job
            if (comp.getId()!= j.getIntCompanyID()){
                comp = dbservice.GetCompanyById(j.getIntCompanyID());
                // if the company has not been created now and cant be found the job will not be further processed
                if (comp.getId()<=0){
                    //em.logErrorMessage(String.format("ExecuteJobs processing failed: Job with id %s has invalid companyid",j.getId()));
                    dbservice.UpdateStatus(j, Enums_.Status.PROCESSED_FAILURE);
                    continue;
                }
                // the standard Prompt for Ollama will be set the company name in it
                ollamaService.SetBasePrompt(comp.getStrName());
            }
            try {
                // sending the Request to Ollama
                response = ollamaService.askOllama(j.getStrParagraphs()).get();
                if(!response.contains("response")){
                    //em.logWarningMessage(String.format("ExecuteJobs processing failed: Ollama did not responded correctly (Job-id %s)",j.getId()));
                    dbservice.UpdateStatus(j, Enums_.Status.PROCESSED_FAILURE);
                    continue;
                }
                // the response is in a json format so it will be casted in a json object
                JSONObject jsonResponseObject = new JSONObject(response);
                // the section response holds the created answer/result from the service
                String strResponse = jsonResponseObject.getString("response");
                //although the prompt to the service is very clear, it still have it own interpretation how to answer
                //so the result will be edited so we can parse it
                strResponse = strResponse.replace("{","").replace("}","").replace(",",";").replace(":","=");
                String[] responseParts = strResponse.split(";");
                int positive = 0;
                int negative =0;
                // if the response was still send in a not readable format this job will give a result of 0/0
                try {
                    positive = Integer.parseInt(responseParts[0].split("=")[1]);
                    negative = Integer.parseInt(responseParts[1].split("=")[1]);
                } catch (NumberFormatException e) {
                    dbservice.UpdateStatus(j, Enums_.Status.PROCESSED_FAILURE);
                    em.logErrorMessage(e);
                    //em.logErrorMessage(String.format("ExecuteJobs processing failed: Ollama response format incorrect (Job-id %s)",j.getId()));
                    continue;
                }
                //adding the result to the DB
                dbservice.addResult(j.getId(), comp.getId(), positive, negative);

            } catch (InterruptedException | ExecutionException e) {
                em.logErrorMessage(e);
                //em.logErrorMessage(String.format("ExecuteJobs (Job-id %s):%s",j.getId(),e.getMessage()));
            }
            dbservice.UpdateStatus(j, Enums_.Status.PROCESSED);
        }
    }


}
