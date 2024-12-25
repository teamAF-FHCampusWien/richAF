package at.ac.fhcampuswien.richAF.model;

import com.j256.ormlite.field.DatabaseField;

import java.sql.Timestamp;

public abstract class baseTbl {
    @DatabaseField
    private int intStatus;

    @DatabaseField
    private Timestamp tsCreated_on;

    public int getIntStatus() {
        return intStatus;
    }

    public Enums.Status getStatus() {
        return Enums.Status.values()[intStatus];
    }

    public void setIntStatus(int intStatus) {
        this.intStatus = intStatus;
    }

    public void setStatus(Enums.Status status) {
        this.intStatus = status.ordinal();
    }

    public Timestamp getTsCreated_on() {
        return tsCreated_on;
    }

    public void setTsCreated_on(Timestamp tsCreated_on) {
        this.tsCreated_on = tsCreated_on;
    }
}
