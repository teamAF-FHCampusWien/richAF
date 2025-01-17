package at.ac.fhcampuswien.richAF.model;

import java.io.*;
import java.util.Properties;

/**
 * Config Properties Class
 * copyright: <a href="https://docs.oracle.com/javase/tutorial/essential/environment/properties.html">Oracle</a>
 * @author Stefan
 */
public class Config {
    private Properties properties = new Properties();
    private Properties userproperties = new Properties();
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


        refreshUserProperties();

    }

    /**
     * returns the requested Entry of the property-file by the key
     * @param key key String of the Property Name
     * @return
     */
    public String getProperty(String key) {
        String value = userproperties.getProperty(key);
        if (value == null)
            return properties.getProperty(key);
        else
            return value;
    }

    /**
     * laods the properties form the user configuration file
     */
    private void refreshUserProperties() {
        try (InputStream input = new FileInputStream(this.getProperty("userconfig"))) {
            if (input == null) {
                System.out.println("ERROR: config.properties not found");
                return;
            }
            userproperties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Save User Config key pair to the user config
     * @param key
     * @param value
     * @author Timmo, Stefan
     */
    public void saveDataToUserConfig(String key, String value) {
        String configFilePath = this.getProperty("userconfig");
        File configFile = new File(configFilePath);

        if (configFile.exists()) {
            try (InputStream in = new FileInputStream(configFile)) {
                userproperties.load(in);
            } catch (IOException e) {
                System.err.println("Failed to load existing config.properties");
                e.printStackTrace();
                return;
            }
        }

        userproperties.setProperty(key, value);

        try (FileOutputStream out = new FileOutputStream(configFilePath)) {
            userproperties.store(out, "Updated by DevMenuController");
        } catch (IOException e) {
            System.err.println("Error writing temporary file");
            e.printStackTrace();
            return;
        }
    }
}
