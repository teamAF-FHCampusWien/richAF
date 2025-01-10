package at.ac.fhcampuswien.richAF.model.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * @deprecated this class and the concept behind it does not suit anymore our needs
 * Model Class for the DAO Table Models
 * corresponding Table tblCompany
 * containing properties with their Getters and Setters
 * properties/tablecolumns:
 *      id
 *      strName
 * @author Stefan
 */
@Deprecated
@DatabaseTable(tableName = "tblCompany")
 class tblCompany {
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

    @Override
    public String toString() {
        return strName;
    }


}
