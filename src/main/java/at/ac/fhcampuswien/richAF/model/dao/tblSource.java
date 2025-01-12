package at.ac.fhcampuswien.richAF.model.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model Class for the DAO Table Models
 * corresponding Table tblSources
 * containing properties with their Getters and Setters
 * properties/tablecolumns:
 *      id
 *      strUrl
 *      strName
 *      intStatus from baseTbl
 *      tsCreated_on from baseTbl
 * @author Stefan
 */
@DatabaseTable(tableName = "tblSources")
public class tblSource extends baseTbl {
    /**
     * id ...  autoincrement integer value
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * strUrl ... the url of the source
     */
    @DatabaseField
    private String strUrl;
    @DatabaseField
    private String strName;



    public tblSource() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrUrl() {
        return strUrl;
    }

    public void setStrUrl(String strUrl) {
        this.strUrl = strUrl;
    }

    public String getName() {
        return strName;
    }

    public void setName(String strName) {
        this.strName = strName;
    }
}
