package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model Class for the DAO Table Models
 * corresponding Table tblPages
 * containing properties with their Getters and Setters
 * properties/tablecolumns:
 *      id
 *      strPage
 *      strKeyword
 *      intStatus from baseTbl
 *      tsCreated_on from baseTbl
 * @author Stefan
 */
@DatabaseTable(tableName = "tblPages")
public class tblPage extends baseTbl {
    /**
     * id ...  autoincrement integer value
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * strPage ... the webpage html text from the webcrawler
     */
    @DatabaseField
    private String strPage;

    /**
     * strKeyword ... a keyword / company name which this page is explicit referred to
     * value "" means that this webpage is for all companies relevant
     */
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
