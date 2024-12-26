package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model Class for the DAO Table Models
 * corresponding Table tblResults
 * containing properties with their Getters and Setters
 * properties/tablecolumns:
 *      id
 *      intJobID
 *      intCompanyID
 *      intPositiv
 *      intNegativ
 *      intStatus from baseTbl
 *      tsCreated_on from baseTbl
 * @author Stefan
 */
@DatabaseTable(tableName = "tblResults")
public class tblResult extends baseTbl {
    public tblResult() {

    }
    /**
     * id ...  autoincrement integer value
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * intJobID  ... the Id of the Job from tblJobs which produces this result
     */
    @DatabaseField
    private Integer intJobID;

    /**
     * intCompanyID ... the id of the Company from tblCompany which this result is assigned
     */
    @DatabaseField
    private Integer intCompanyID;

    /**
     * intPositiv ... number of positive news to the company the ollama service found in the text of the job
     */
    @DatabaseField
    private Integer intPositiv;
    /**
     * intPositiv ... number of negative news to the company the ollama service found in the text of the job
     */
    @DatabaseField
    private Integer intNegativ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIntJobID() {
        return intJobID;
    }

    public void setIntJobID(Integer intJobID) {
        this.intJobID = intJobID;
    }

    public Integer getIntCompanyID() {
        return intCompanyID;
    }

    public void setIntCompanyID(Integer intCompanyID) {
        this.intCompanyID = intCompanyID;
    }

    public Integer getIntPositiv() {
        return intPositiv;
    }

    public void setIntPositiv(Integer intPositiv) {
        this.intPositiv = intPositiv;
    }

    public Integer getIntNegativ() {
        return intNegativ;
    }

    public void setIntNegativ(Integer intNegativ) {
        this.intNegativ = intNegativ;
    }
}
