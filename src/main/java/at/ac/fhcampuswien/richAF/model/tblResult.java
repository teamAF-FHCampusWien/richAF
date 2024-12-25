package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "tblResults")
public class tblResult extends baseTbl {
    public tblResult() {

    }
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private Integer intJobID;


    @DatabaseField
    private Integer intCompanyID;

    @DatabaseField
    private Integer intPositiv;
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
