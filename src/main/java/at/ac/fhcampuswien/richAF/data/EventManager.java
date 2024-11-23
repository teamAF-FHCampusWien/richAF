package at.ac.fhcampuswien.richAF.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EventManager {
    private String fileName;
    private String timeFormat;
    public static final String INFO = "INFO";
    public static final String WARNING = "WARNING";
    public static final String ERROR = "ERROR";
    public static List<String> availableTimeFormats = Arrays.asList("dd-MMM-yyyy HH:mm:ss z", "dd.MM.yyyy h:mm:ss.SSS a z", "E, MMM dd yyyy HH:mm:ss z");

    public EventManager(String fileName, String timeFormat) {
        this.fileName = fileName;
        this.timeFormat = timeFormat;
    }

    public EventManager(String fileName) {
        this.fileName = fileName;
        this.timeFormat = availableTimeFormats.get(0);
    }

    public EventManager() {
        this.fileName = "/tmp/richAF.log";
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
     * @thows IOException On input error.
    * */
    public void logMessage(String message, String level) {
        // Get the class name of the caller
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        String callerClassName = stackTraceElement[2].getClassName();

        // Get the current time as a string in the specified format
        String time = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(this.timeFormat));

        try {
            // Write to file and create it if it does not exist
            if(!new File(this.fileName).exists()){
                this.fileName = String.valueOf(new File(this.fileName).createNewFile());
            }
            FileWriter myWriter = new FileWriter(this.fileName, true);
            myWriter.write(String.format("[%s] %s %s: %s\n", time, level, callerClassName, message));
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred:"+e.getMessage());
        }
    }
}
