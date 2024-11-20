package at.ac.fhcampuswien.richAF.DataManager;

public class LogManager {
    private String fileName;

    public LogManager(String fileName) {
        this.fileName = fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
