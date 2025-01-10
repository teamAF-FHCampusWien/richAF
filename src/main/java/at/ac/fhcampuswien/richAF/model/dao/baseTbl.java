package at.ac.fhcampuswien.richAF.model.dao;
import at.ac.fhcampuswien.richAF.model.Enums_;
import com.j256.ormlite.field.DatabaseField;
import java.sql.Timestamp;


/**
 * Base Model Class for the DAO Table Models
 * containing properties with their Getters and Setters
 * properties:
 *      intStatus
 *      tsCreated_on
 * @author Stefan
 */
public abstract class baseTbl {
    /**
     * intStatus ... the ordinal value of the enumerator in Enums_.Status
     */
    @DatabaseField
    private int intStatus;

    /**
     * tsCreated_on ... Timestamp value when the line was inserted to the table
     */
    @DatabaseField
    private Timestamp tsCreated_on;

    public int getIntStatus() {
        return intStatus;
    }

    public Enums_.Status getStatus() {
        return Enums_.Status.values()[intStatus];
    }

    public void setIntStatus(int intStatus) {
        this.intStatus = intStatus;
    }

    public void setStatus(Enums_.Status status) {
        this.intStatus = status.ordinal();
    }

    public Timestamp getTsCreated_on() {
        return tsCreated_on;
    }

    public void setTsCreated_on(Timestamp tsCreated_on) {
        this.tsCreated_on = tsCreated_on;
    }
}
