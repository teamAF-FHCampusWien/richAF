package at.ac.fhcampuswien.richAF.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Config Properties Class
 * copyright: <a href="https://docs.oracle.com/javase/tutorial/essential/environment/properties.html">Oracle</a>
 * @author Stefan
 */
public class Config {
    private Properties properties = new Properties();

    /**
     * Constructor: searches for a file config.properties in the resources directory and reads the property list from it
     */
    public Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("ERROR: config.properties not found");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * returns the requested Entry of the property-file by the key
     * @param key key String of the Property Name
     * @return
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
