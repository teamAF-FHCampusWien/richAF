package at.ac.fhcampuswien.richAF.data;

import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EventManager {
    private String fileName;
    private String timeFormat;
    public static List<String> availableTimeFormats = Arrays.asList("dd-MMM-yyyy HH:mm:ss z", "dd.MM.yyyy h:mm:ss a z", "E, MMM dd yyyy HH:mm:ss z");

    public EventManager(String fileName, String timeFormat) {
        this.fileName = fileName;
        this.timeFormat = timeFormat;
    }

    public EventManager(String fileName) {
        this.fileName = fileName;
        this.timeFormat = availableTimeFormats.get(0);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
    * Log a message to the destination file.
     *
     * @param message The message to log.
     * @param className The name of the class that called the method.
     *
    * */
    public void logMessage(String message, String className) {
        String time = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(this.timeFormat));
        System.out.printf("%s [%s]: %s\n", time, className, message);
    }
}
