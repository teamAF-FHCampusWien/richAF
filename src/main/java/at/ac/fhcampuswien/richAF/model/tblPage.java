package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "tblPages")
public class tblPage extends baseTbl {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String strPage;

    @DatabaseField
    private String strKeyword;


    public tblPage() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrPage() {
        return strPage;
    }

    public void setStrPage(String strPage) {
        this.strPage = strPage;
    }

    public String getStrKeyword() {
        return strKeyword;
    }

    public void setStrKeyword(String strKeyword) {
        this.strKeyword = strKeyword;
    }







}
