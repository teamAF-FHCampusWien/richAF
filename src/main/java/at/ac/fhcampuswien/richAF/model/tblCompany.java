package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * Model Class for the DAO Table Models
 * corresponding Table tblCompany
 * containing properties with their Getters and Setters
 * properties/tablecolumns:
 *      id
 *      strName
 * @author Stefan
 */
@DatabaseTable(tableName = "tblCompany")
public class tblCompany {
    public tblCompany() {

    }

    /**
     * id ...  autoincrement integer value
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * strName ... Name of the Company
     */
    @DatabaseField
    private String strName;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }




}
