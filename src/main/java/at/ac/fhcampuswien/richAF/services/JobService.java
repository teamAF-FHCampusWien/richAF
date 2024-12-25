package at.ac.fhcampuswien.richAF.services;


import at.ac.fhcampuswien.richAF.model.*;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class JobService {


    public static void CreateJobs( DBService dbservice, int pcounter, int companyid) {
        ArrayList<tblPage> lisPages = dbservice.getPages(Enums_.Status.NEW, "");
        if (pcounter < 1) return;

        for (tblPage p : lisPages) {
            Document doc = Jsoup.parse(p.getStrPage());
            dbservice.UpdateStatus(p, Enums_.Status.PROCESSING);
            try {
                String doctype = doc.documentType().name();
                if (doctype.equals("html")) {
                    Elements paragraphs= doc.select("p");
                    String strParagraph="";
                    for (int i = 0; i < paragraphs.size(); i++) {
                        strParagraph = strParagraph + paragraphs.get(i).text();
                        if ((i+1) % pcounter == 0) {
                            dbservice.addJob(strParagraph,companyid);
                            strParagraph = "";

                        }
                    }
                    if (strParagraph != "")
                        dbservice.addJob(strParagraph,companyid);

                    dbservice.UpdateStatus(p, Enums_.Status.PROCESSED);
                }
                else dbservice.UpdateStatus(p, Enums_.Status.PROCESSED_FAILURE);


            } catch (Exception e) {
                System.out.println(e);
            }
        }



    }

    public static void ExecuteJobs(OllamaService ollamaService, DBService dbservice) {
        ArrayList<tblJob> lisJobs = dbservice.getJobs(Enums_.Status.NEW);
        tblCompany comp=new tblCompany();

        for (tblJob j : lisJobs) {
            String response = "";
            dbservice.UpdateStatus(j, Enums_.Status.PROCESSING);
            if (comp.getId()!= j.getIntCompanyID()){
                comp = dbservice.GetCompanyById(j.getIntCompanyID());
                if (comp.getId()<=0){
                    System.out.println("Jobs cant be processed, company not found");
                    dbservice.UpdateStatus(j, Enums_.Status.PROCESSED_FAILURE);
                    continue;
                }
                ollamaService.SetBasePrompt(comp.getStrName());
            }
            try {
                response = ollamaService.askOllama(j.getStrParagraphs()).get();  //"format": {"type": "json","properties": {"positive": {"type": "integer"},"negative": {"type": "integer"}}, "required": ["positive", "negative"]}

                if(!response.contains("response")){
                    dbservice.UpdateStatus(j, Enums_.Status.PROCESSED_FAILURE);
                    continue;
                }
                JSONObject jsonResponseObject = new JSONObject(response);

                String strResponse = jsonResponseObject.getString("response");
                strResponse = strResponse.replace("{","").replace("}","").replace(",",";").replace(":","=");
                String[] responseParts = strResponse.split(";");
                int positive = 0;
                int negative =0;
                try {
                     positive = Integer.parseInt(responseParts[0].split("=")[1]);
                    negative = Integer.parseInt(responseParts[1].split("=")[1]);
                } catch (NumberFormatException e) {
                    dbservice.UpdateStatus(j, Enums_.Status.PROCESSED_FAILURE);
                    continue;
                }

                dbservice.addResult(j.getId(), comp.getId(), positive, negative);

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }




            dbservice.UpdateStatus(j, Enums_.Status.PROCESSED);
        }


    }
}
