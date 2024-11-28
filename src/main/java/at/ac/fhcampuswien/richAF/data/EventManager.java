package at.ac.fhcampuswien.richAF.data;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EventManager {
    private String fileName;
    private String timeFormat;
    public List<String> availableTimeFormats;
    public static final String INFO = "INFO";
    public static final String WARNING = "WARNING";
    public static final String ERROR = "ERROR";

    public EventManager(String fileName, String timeFormat) {
        this.fileName = fileName;
        if(isValidTimeFormat(timeFormat)){
            this.timeFormat = timeFormat;
        } else {
            this.timeFormat = "dd-MMM-yyyy HH:mm:ss z";
        }
        readTimeFormats();
    }

    public EventManager(String fileName) {
        this.fileName = fileName;
        readTimeFormats();
        this.timeFormat = availableTimeFormats.get(0);
    }

    public EventManager() {
        this.fileName = "/tmp/richAF.log";
        readTimeFormats();
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

    private void readTimeFormats() {
        String path = System.getProperty("user.dir") + "/src/main/resources/timeFormats.txt";
        BufferedReader reader;
        List<String> formats = null;
        List<String> fileContent = null;
        List<String> replacement = Arrays.asList("dd-MMM-yyyy HH:mm:ss z", "dd.MM.yyyy h:mm:ss.SSS a z", "E, MMM dd yyyy HH:mm:ss z");

        // Error handling of the file processing part
        try {
            // Read the file and return its content as a list of strings
            reader = new BufferedReader(new FileReader(path));
            fileContent = reader.lines().toList();
        } catch (IOException e) {
            this.timeFormat = replacement.get(0);
            logMessage("An error occurred:"+e.getMessage(), ERROR);
            this.availableTimeFormats = replacement;
            return;
        }

        // Error handling of the time format validation
        try{
            for(String line : fileContent){
                if(isValidTimeFormat(line)){
                    formats.add(line);
                }
            }
        } catch (Exception e){
            this.timeFormat = replacement.get(0);
            logMessage("An error occurred:"+e.getMessage(), ERROR);
            this.availableTimeFormats = replacement;
            return;
        }
        this.availableTimeFormats = formats;
    }

    private boolean isValidTimeFormat(String timeFormat) {
        try {
            DateTimeFormatter.ofPattern(timeFormat);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
    * Log a message to the destination file.
     *
     * @param message The message to log.
     * @param level The log level of the message.
     *
     * @thows IOException On input error.
    * */
    public void logMessage(String message, String level) {
        // Get the class name and method of the caller
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        String callerClassName = stackTraceElement[2].getClassName();
        String callerMethodName = stackTraceElement[2].getMethodName();

        // Get the current time as a string in the specified format
        String time = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(this.timeFormat));

        try {
            // Check if the file exists and create it if it doesn't
            if(!new File(this.fileName).exists()){
                this.fileName = String.valueOf(new File(this.fileName).createNewFile());
            }
            // Write the event to the file
            FileWriter myWriter = new FileWriter(this.fileName, true);
            myWriter.write(String.format("[%s] %s %s %s: %s\n", time, level, callerClassName, callerMethodName, message));
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred:"+e.getMessage());
        }
    }

    /**
     * Read the log file and return its content as a list of strings.
     *
     * @return The content of the log file as a list of strings.
     * @thows IOException On input error.
     */
    public List<String> readLogFile(){
        BufferedReader reader;
        try {
            // Read the file and return its content as a list of strings
            reader = new BufferedReader(new FileReader(this.fileName));
            return reader.lines().toList();
        } catch (IOException e) {
            System.out.println("An error occurred:"+e.getMessage());
        }
        return null;
    }
}
