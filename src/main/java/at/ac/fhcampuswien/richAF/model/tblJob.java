package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model Class for the DAO Table Models
 * corresponding Table tblJobs
 * containing properties with their Getters and Setters
 * properties/tablecolumns:
 *      id
 *      strParagraphs
 *      intCompanyID
 *      intStatus from baseTbl
 *      tsCreated_on from baseTbl
 * @author Stefan
 */
@DatabaseTable(tableName = "tblJobs")
public class tblJob extends baseTbl {

    /**
     * id ...  autoincrement integer value
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * strParagraphs ... The Text paragraphs of the webpage which should be processed
     */
    @DatabaseField
    private String strParagraphs;


    /**
     * intCompanyID ... ID from the tblCompany of the company which this job should be referred to
     */
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
