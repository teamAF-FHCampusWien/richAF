package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "tblJobs")
public class tblJob extends baseTbl {


    @DatabaseField(generatedId = true)
        private int id;

        @DatabaseField
        private String strParagraphs;



    @DatabaseField
        private Integer intCompanyID;


        public tblJob() {

        }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrParagraphs() {
        return strParagraphs;
    }

    public void setStrParagraphs(String strParagraphs) {
        this.strParagraphs = strParagraphs;
    }


    public Integer getIntCompanyID() {
        return intCompanyID;
    }

    public void setIntCompanyID(Integer intCompanyID) {
        this.intCompanyID = intCompanyID;
    }

}
