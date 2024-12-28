package at.ac.fhcampuswien.richAF.model;

/**
 * Class containing Enumerations used in the project
 * @author Stefan
 */
public class Enums_ {
    /**
     * Enumeration of Status-values used for Status in the SQL-Tables
     */
    public static enum Status {
        NEW,
        ALL,
        RUNNING,
        DONE,
        HIDDEN,
        PROCESSING,
        PROCESSED,
        PROCESSED_FAILURE
    }
}
