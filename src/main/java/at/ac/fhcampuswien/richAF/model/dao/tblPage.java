package at.ac.fhcampuswien.richAF.model.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model Class for the DAO Table Models
 * corresponding Table tblPages
 * containing properties with their Getters and Setters
 * properties/tablecolumns:
 *      id
 *      strPage
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
     * the Title of the html code
     */
    @DatabaseField
    private String strTitle;

    /**
     * the link of the source
     */
    @DatabaseField
    private String strLink;





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


    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrLink() {
        return strLink;
    }

    public void setStrLink(String strLink) {
        this.strLink = strLink;
    }
}
